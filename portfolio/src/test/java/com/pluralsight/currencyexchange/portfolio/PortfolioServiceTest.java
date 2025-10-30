package com.pluralsight.currencyexchange.portfolio;

import com.pluralsight.currencyexchange.currency.CurrencyRate;
import com.pluralsight.currencyexchange.portfolio.trade.Trade;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

@QuarkusTest
class PortfolioServiceTest {

  @Inject
  PortfolioService portfolioService;

  @BeforeEach
  void mock() {
    PortfolioService mock = Mockito.mock(PortfolioService.class);

    Mockito.when(mock.getAllCurrentRates()).thenReturn(List.of(
      CurrencyRate.newBuilder().setCurrencyCode("AUD").setRate(1.5234).build(),
      CurrencyRate.newBuilder().setCurrencyCode("CAD").setRate(1.3425).build(),
      CurrencyRate.newBuilder().setCurrencyCode("CHF").setRate(1.3425).build(),
      CurrencyRate.newBuilder().setCurrencyCode("EUR").setRate(0.9217).build(),
      CurrencyRate.newBuilder().setCurrencyCode("GBP").setRate(0.7905).build(),
      CurrencyRate.newBuilder().setCurrencyCode("JPY").setRate(149.25).build()
    ));

    Mockito.when(mock.getCurrentRate("AUD")).thenReturn(CurrencyRate.newBuilder().setCurrencyCode("AUD").setRate(1.5234).build());
    Mockito.when(mock.getCurrentRate("CAD")).thenReturn(CurrencyRate.newBuilder().setCurrencyCode("CAD").setRate(1.3425).build());
    Mockito.when(mock.getCurrentRate("CHF")).thenReturn(CurrencyRate.newBuilder().setCurrencyCode("CHF").setRate(1.3425).build());
    Mockito.when(mock.getCurrentRate("EUR")).thenReturn(CurrencyRate.newBuilder().setCurrencyCode("EUR").setRate(0.9217).build());
    Mockito.when(mock.getCurrentRate("GBP")).thenReturn(CurrencyRate.newBuilder().setCurrencyCode("GBP").setRate(0.7905).build());
    Mockito.when(mock.getCurrentRate("JPY")).thenReturn(CurrencyRate.newBuilder().setCurrencyCode("JPY").setRate(149.25).build());

    Mockito.when(mock.getAllTrades("user456")).thenReturn(List.of(new Trade("user456", BigDecimal.valueOf(100), "EUR", new BigDecimal("92.00"), BigDecimal.valueOf(0.92))));

    QuarkusMock.installMockForType(mock, PortfolioService.class);
  }

  @Test
  void shouldGetAllCurrentRates() throws Exception {
    List<CurrencyRate> currencyRates = portfolioService.getAllCurrentRates();

    assertNotNull(currencyRates);
    assertEquals(6, currencyRates.size());

  }

  @ParameterizedTest
  @ValueSource(strings = {"AUD", "CAD", "CHF", "EUR", "GBP", "JPY"})
  void shouldGetCurrentRate(String currencyCode) {
    CurrencyRate currencyRate = portfolioService.getCurrentRate(currencyCode);

    assertNotNull(currencyRate);
    assertEquals(currencyCode, currencyRate.getCurrencyCode());
    assertTrue(currencyRate.getRate() > 0);
    assertNotNull(currencyRate.getTimestamp());
  }

  @Test
  void shouldExecuteAndGetTrades() {
    // First, execute a trade
    Trade trade = new Trade("user456", BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(0.92));

    portfolioService.executeTrade(trade);

    List<Trade> trades = portfolioService.getAllTrades("user456");

    assertNotNull(trades);
    assertFalse(trades.isEmpty());
    assertEquals("user456", trades.getFirst().userId());
    assertEquals("EUR", trades.getFirst().toCurrency());
    assertEquals(new BigDecimal(100), trades.getFirst().usdAmount());
    assertEquals(new BigDecimal("0.92"), trades.getFirst().exchangeRate());
    assertEquals(new BigDecimal("92.00"), trades.getFirst().convertedAmount());
    String status = trades.getFirst().status();
    assertTrue(status.equals("COMPLETED") || status.equals("CREATED"));
    assertNotNull(trades.getFirst().timestamp());
  }
}