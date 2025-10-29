package com.pluralsight.currencyexchange.portfolio.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Trade(

  String userId,
  LocalDateTime timestamp,
  BigDecimal usdAmount,
  String toCurrency,
  BigDecimal convertedAmount,
  BigDecimal exchangeRate,
  String status
) {

  // Constructor for creating new trades (before execution)
  public Trade(String userId, BigDecimal usdAmount, String toCurrency, BigDecimal exchangeRate) {
    this(userId, LocalDateTime.now(), usdAmount, toCurrency, null, exchangeRate, "CREATED");
  }

  public Trade(String userId, BigDecimal usdAmount, String toCurrency, BigDecimal convertedAmount, BigDecimal exchangeRate) {
    this(userId, LocalDateTime.now(), usdAmount, toCurrency, convertedAmount, exchangeRate, "CREATED");
  }

  public Trade(String email, BigDecimal usdAmount, String toCurrency, double rate) {
    this(email, LocalDateTime.now(), usdAmount, toCurrency, null, BigDecimal.valueOf(rate), "CREATED");
  }
}

