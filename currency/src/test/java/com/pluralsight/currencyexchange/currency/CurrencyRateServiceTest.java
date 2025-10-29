package com.pluralsight.currencyexchange.currency;

import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@QuarkusTest
class CurrencyRateServiceTest {

  @GrpcClient
  CurrencyRateService currencyRateService;

  @Test
  void shouldGetCurrentRates() throws Exception {
    Empty request = Empty.newBuilder().build();

    CompletableFuture<CurrencyRateListResponse> message = new CompletableFuture<>();
    currencyRateService.getAllCurrentRates(request).subscribe().with(
      reply -> message.complete(reply)
    );

    CurrencyRateListResponse response = message.get(5, TimeUnit.SECONDS);

    assertNotNull(response);
    assertEquals(6, response.getCurrencyRatesCount());

  }

  @ParameterizedTest
  @ValueSource(strings = {"AUD", "CAD", "CHF", "EUR", "GBP", "JPY"})
  void shouldGetCurrentRate(String currencyCode) throws Exception {
    CurrencyRequest request = CurrencyRequest.newBuilder()
      .setCurrencyCode(currencyCode)
      .build();

    CompletableFuture<CurrencyRateResponse> message = new CompletableFuture<>();
    currencyRateService.getCurrentRate(request).subscribe().with(
      reply -> message.complete(reply)
    );

    CurrencyRateResponse response = message.get(5, TimeUnit.SECONDS);

    assertNotNull(response);
    assertNotNull(response.getCurrencyRate());

    CurrencyRate rate = response.getCurrencyRate();
    assertEquals(currencyCode, rate.getCurrencyCode());
    assertTrue(rate.getRate() > 0);
    assertNotNull(rate.getTimestamp());
  }
}