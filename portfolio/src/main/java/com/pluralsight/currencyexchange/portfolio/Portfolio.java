package com.pluralsight.currencyexchange.portfolio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Portfolio(
  Long id,
  User user,
  String currency,
  BigDecimal balance,
  LocalDateTime lastUpdated
) {
}