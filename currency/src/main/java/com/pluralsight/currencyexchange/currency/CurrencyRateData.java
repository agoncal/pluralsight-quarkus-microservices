package com.pluralsight.currencyexchange.currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public record CurrencyRateData(
  String currencyCode,     // Target currencyCode code (EUR, GBP, JPY, etc.)
  BigDecimal rate,         // Exchange rate (1 USD = 0.9217 EUR)
  LocalDateTime timestamp  // When the rate was calculated
) {
  public static final Set<String> SUPPORTED_CURRENCIES = Set.of(
    "AUD", "CAD", "CHF", "EUR", "GBP", "JPY"
  );

  public static final Map<String, BigDecimal> EXCHANGE_RATES = Map.of(
    "AUD", BigDecimal.valueOf(1.5234),
    "CAD", BigDecimal.valueOf(1.3425),
    "CHF", BigDecimal.valueOf(0.9156),
    "EUR", BigDecimal.valueOf(0.9217),
    "GBP", BigDecimal.valueOf(0.7905),
    "JPY", BigDecimal.valueOf(149.25)
  );
}