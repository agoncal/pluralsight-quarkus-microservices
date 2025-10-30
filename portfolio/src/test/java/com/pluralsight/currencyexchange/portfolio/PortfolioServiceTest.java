package com.pluralsight.currencyexchange.portfolio;

import com.pluralsight.currencyexchange.currency.CurrencyRate;
import com.pluralsight.currencyexchange.portfolio.trade.Trade;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

@QuarkusTest
class PortfolioServiceTest {

  @Inject
  PortfolioService portfolioService;

  @Test
  void shouldGetCurrentRates() throws Exception {
    List<CurrencyRate> currencyRates = portfolioService.getAllCurrentRates();

    assertNotNull(currencyRates);
    assertEquals(6, currencyRates.size());

  }

  @ParameterizedTest
  @ValueSource(strings = {"AUD", "CAD", "CHF", "EUR", "GBP", "JPY"})
  void shouldGetCurrentRate(String currencyCode) throws Exception {
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
    assertEquals(100, trades.getFirst().usdAmount());
    assertEquals(0.92f, trades.getFirst().exchangeRate());
    assertEquals(92.00f, trades.getFirst().convertedAmount());
    assertEquals("COMPLETED", trades.getFirst().status());
    assertNotNull(trades.getFirst().timestamp());
  }
}