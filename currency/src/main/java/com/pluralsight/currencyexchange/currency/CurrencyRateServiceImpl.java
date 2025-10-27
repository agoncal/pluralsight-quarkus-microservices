package com.pluralsight.currencyexchange.currency;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@GrpcService
public class CurrencyRateServiceImpl extends CurrencyRateServiceGrpc.CurrencyRateServiceImplBase {

    private static final Logger LOG = Logger.getLogger(CurrencyRateServiceImpl.class);

    /**
     * Currency-specific seeds used in the rate fluctuation algorithm.
     * Each currency gets a unique seed value that is added to the current timestamp
     * in the sin() function to create different fluctuation patterns for each currency.
     * This ensures that different currencies don't fluctuate in sync and creates
     * more realistic, independent exchange rate movements.
     */
    private static final Map<String, Long> CURRENCY_SEEDS = Map.of(
        "AUD", 1000L,
        "CAD", 2000L,
        "CHF", 3000L,
        "EUR", 4000L,
        "GBP", 5000L,
        "JPY", 6000L
    );

    @Override
    public void getAllCurrentRates(Empty request, StreamObserver<CurrencyRateListResponse> responseObserver) {
        LOG.info("Getting all current exchange rates");

        try {

            List<CurrencyRateData> currencyRates = getAllCurrentRates();

            CurrencyRateListResponse.Builder responseBuilder = CurrencyRateListResponse.newBuilder();

            for (CurrencyRateData currencyRate : currencyRates) {
                responseBuilder.addCurrencyRates(convertToGrpc(currencyRate));
            }

            CurrencyRateListResponse response = responseBuilder.build();
            LOG.info("Returning " + currencyRates.size() + " currency rates");

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting all current rates", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getCurrentRate(CurrencyRequest request, StreamObserver<CurrencyRateResponse> responseObserver) {
        LOG.info("Getting current rate for currency: " + request.getCurrencyCode());

        try {

            CurrencyRateData currencyRate = getCurrencyRate(request.getCurrencyCode());

            CurrencyRateResponse response = CurrencyRateResponse.newBuilder()
                .setCurrencyRate(convertToGrpc(currencyRate))
                .build();

            LOG.info("Returning exchange rate for " + request.getCurrencyCode() + ": " + currencyRate.rate());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting current rate for " + request.getCurrencyCode(), e);
            responseObserver.onError(e);
        }
    }

    private List<CurrencyRateData> getAllCurrentRates() {
        return CurrencyRateData.SUPPORTED_CURRENCIES.stream()
            .map(currencyCode -> calculateRate(currencyCode))
            .toList();
    }

    private CurrencyRateData getCurrencyRate(String currencyCode) {
        if (!CurrencyRateData.SUPPORTED_CURRENCIES.contains(currencyCode)) {
            return null;
        }
        return calculateRate(currencyCode);
    }

    private CurrencyRate convertToGrpc(CurrencyRateData rate) {
        return CurrencyRate.newBuilder()
            .setCurrencyCode(rate.currencyCode())
            .setRate(rate.rate().doubleValue())
            .setTimestamp(rate.timestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }

    private CurrencyRateData calculateRate(String currencyCode) {
        BigDecimal baseRate = CurrencyRateData.EXCHANGE_RATES.get(currencyCode);
        if (baseRate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currencyCode);
        }

        // Generate fluctuation using current timestamp + currency-specific seed
        long seed = CURRENCY_SEEDS.get(currencyCode);
        long currentTime = System.currentTimeMillis() / 1_000; // Convert to seconds
        double fluctuation = Math.sin(currentTime + seed) * 0.2;

        BigDecimal rate = baseRate.add(BigDecimal.valueOf(fluctuation))
            .setScale(4, RoundingMode.HALF_UP);

        // Ensure JPY has appropriate scale (2 decimal places)
        if ("JPY".equals(currencyCode)) {
            rate = rate.setScale(2, RoundingMode.HALF_UP);
        }

        return new CurrencyRateData(currencyCode, rate, LocalDateTime.now());
    }
}
