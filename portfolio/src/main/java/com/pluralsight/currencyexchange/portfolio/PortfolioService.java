package com.pluralsight.currencyexchange.portfolio;

import com.google.protobuf.Empty;
import com.pluralsight.currencyexchange.currency.CurrencyRate;
import com.pluralsight.currencyexchange.currency.CurrencyRateServiceGrpc;
import com.pluralsight.currencyexchange.currency.CurrencyRequest;
import static com.pluralsight.currencyexchange.portfolio.User.USER_PORTFOLIOS;
import com.pluralsight.currencyexchange.portfolio.trade.Trade;
import com.pluralsight.currencyexchange.portfolio.trade.TradeService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class PortfolioService {

  private static final Logger LOG = Logger.getLogger(PortfolioService.class);

  @GrpcClient("currency")
  CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub exchangeRateService;

  @RestClient
  TradeService tradeService;
  
  @Inject
  MeterRegistry meterRegistry;

  public List<Portfolio> getUserPortfolio(String userId) {
    LOG.info("Get portfolio for user " + userId);

    return USER_PORTFOLIOS.getOrDefault(userId, List.of())
      .stream()
      .sorted(Comparator.comparing(Portfolio::currency))
      .toList();
  }

  @Fallback(fallbackMethod = "fallbackGetAllCurrentRates")
  @Timed(value = "mymetric.portfolio.getAllCurrentRates")
  public List<CurrencyRate> getAllCurrentRates() {
    LOG.info("Get all currency rates");

    return exchangeRateService.getAllCurrentRates(Empty.newBuilder().build()).getCurrencyRatesList();
  }

  @Fallback(fallbackMethod = "fallbackGetCurrentRate")
  @Timed(value = "mymetric.portfolio.getCurrentRate")
  public CurrencyRate getCurrentRate(String currencyCode) {
    LOG.info("Get currency rate: " + currencyCode);

    return exchangeRateService.getCurrentRate(CurrencyRequest.newBuilder().setCurrencyCode(currencyCode).build()).getCurrencyRate();
  }

  @Fallback(fallbackMethod = "fallbackExecuteTrade")
  @Timed(value = "mymetric.portfolio.executeTrade")
  public void executeTrade(Trade trade) {
    LOG.info("Execute trade: " + trade);

    tradeService.executeTrade(trade);
    updateUserPortfolio(trade);
  }

  @Fallback(fallbackMethod = "fallbackGetAllTrades")
  @Retry(maxRetries = 3, delay = 5, delayUnit = ChronoUnit.SECONDS)
  @Timed(value = "mymetric.portfolio.getAllTrades")
  public List<Trade> getAllTrades(String userId) {
    LOG.info("Get all trades");

    return tradeService.getAllTrades(userId);
  }

  public List<CurrencyRate> fallbackGetAllCurrentRates() {
    LOG.warn("Falling back on get all currency rates");
    meterRegistry.counter("mymetric.portfolio.fallback.getAllCurrentRates");
    return List.of(
      CurrencyRate.newBuilder().setCurrencyCode("AUD").setRate(0).build(),
      CurrencyRate.newBuilder().setCurrencyCode("CAD").setRate(0).build(),
      CurrencyRate.newBuilder().setCurrencyCode("CHF").setRate(0).build(),
      CurrencyRate.newBuilder().setCurrencyCode("EUR").setRate(0).build(),
      CurrencyRate.newBuilder().setCurrencyCode("GBP").setRate(0).build(),
      CurrencyRate.newBuilder().setCurrencyCode("JPY").setRate(0).build()
    );
  }

  public CurrencyRate fallbackGetCurrentRate(String currencyCode) {
    LOG.warn("Falling back on get currency rate: " + currencyCode);
    meterRegistry.counter("mymetric.portfolio.fallback.getCurrentRate");

    return CurrencyRate.newBuilder().setCurrencyCode(currencyCode).setRate(0).build();
  }

  private static final List<Trade> FALLBACK_TRADES = new ArrayList<>();

  public void fallbackExecuteTrade(Trade trade) {
    LOG.warn("Falling back on execute trade: " + trade);
    meterRegistry.counter("mymetric.portfolio.fallback.executeTrade");

    FALLBACK_TRADES.add(trade);
  }

  public List<Trade> fallbackGetAllTrades(String userId) {
    LOG.warn("Falling back on get all trades");
    meterRegistry.counter("mymetric.portfolio.fallback.getAllTrades");

    return FALLBACK_TRADES;
  }

  private static void updateUserPortfolio(Trade trade) {
    // Update user portfolio balance for the target currency
    List<Portfolio> userPortfolios = USER_PORTFOLIOS.get(trade.userId());
    if (userPortfolios != null) {
      // Calculate converted amount
      BigDecimal convertedAmount = trade.usdAmount().multiply(trade.exchangeRate());

      // Find the portfolio entry for the target currency
      Portfolio targetPortfolio = userPortfolios.stream()
        .filter(p -> p.currency().equals(trade.toCurrency()))
        .findFirst()
        .orElse(null);

      if (targetPortfolio != null) {
        // Update the balance by adding the converted amount (rounded to 1 decimal)
        BigDecimal newBalance = targetPortfolio.balance()
          .add(convertedAmount)
          .setScale(1, RoundingMode.HALF_UP);
        Portfolio updatedPortfolio = new Portfolio(
          targetPortfolio.id(),
          targetPortfolio.user(),
          targetPortfolio.currency(),
          newBalance,
          java.time.LocalDateTime.now()
        );

        // Replace the old portfolio entry with the updated one
        userPortfolios.remove(targetPortfolio);
        userPortfolios.add(updatedPortfolio);
      }
    }
  }
}
