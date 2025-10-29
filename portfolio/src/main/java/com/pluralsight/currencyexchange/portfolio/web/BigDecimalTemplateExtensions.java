package com.pluralsight.currencyexchange.portfolio.web;

import io.quarkus.qute.TemplateExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

@TemplateExtension
public class BigDecimalTemplateExtensions {

  public static String formatAmount(BigDecimal value) {
    if (value == null) {
      return "-";
    }
    return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
  }
}