package com.pluralsight.currencyexchange.trade;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Currency exchange trade")
public record Trade(

  @Schema(description = "User ID", required = true)
  String userId,

  @Schema(description = "Trade timestamp")
  LocalDateTime timestamp,

  @Schema(description = "Amount in USD to exchange", required = true)
  BigDecimal usdAmount,

  @Schema(description = "Target currency code", required = true, examples = "EUR")
  String toCurrency,

  @Schema(description = "Converted amount in target currency")
  BigDecimal convertedAmount,

  @Schema(description = "Exchange rate applied", required = true)
  BigDecimal exchangeRate,

  @Schema(description = "Trade status", examples = "COMPLETED", enumeration = {"CREATED", "PENDING", "COMPLETED"})
  String status
) {

  // Constructor for creating new trades (before execution)
  public Trade(String userId, BigDecimal usdAmount, String toCurrency, BigDecimal exchangeRate) {
    this(userId, LocalDateTime.now(), usdAmount, toCurrency, null, exchangeRate, "PENDING");
  }
}

