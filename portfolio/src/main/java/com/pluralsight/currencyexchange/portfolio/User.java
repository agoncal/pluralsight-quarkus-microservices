package com.pluralsight.currencyexchange.portfolio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public record User(
    Long id,
    String name,
    String surname,
    String email,
    String number,        // Credit card number (masked for security)
    YearMonth expiryDate, // Expiry date (MM/YY)
    String type          // VISA, MASTERCARD, AMEX, etc.
) {

    // Constructor with masked card number for security
    public User(Long id, String name, String surname, String email, String number, YearMonth expiryDate, String type) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.number = maskCardNumber(number);
        this.expiryDate = expiryDate;
        this.type = type;
    }

    private static String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return number;
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    // Hard-coded users with credit card information
    private static final User USER1 = new User(1L, "John", "Doe", "john.doe@example.com",
        "4532123456781234", YearMonth.of(2026, 12), "VISA");
    private static final User USER2 = new User(2L, "Jane", "Smith", "jane.smith@example.com",
        "5555123456789876", YearMonth.of(2025, 8), "MASTERCARD");
    private static final User USER3 = new User(3L, "Bob", "Johnson", "bob.johnson@example.com",
        "378282246310005", YearMonth.of(2027, 3), "AMEX");

    // Hard-coded portfolios for each user (mutable for trade updates)
    public static final Map<String, List<Portfolio>> USER_PORTFOLIOS = Map.of(
        "john.doe@example.com", new java.util.ArrayList<>(List.of(
            new Portfolio(2L, USER1, "EUR", BigDecimal.valueOf(85.0), LocalDateTime.now()),
            new Portfolio(3L, USER1, "GBP", BigDecimal.valueOf(50.0), LocalDateTime.now()),
            new Portfolio(4L, USER1, "JPY", BigDecimal.valueOf(100.0), LocalDateTime.now()),
            new Portfolio(5L, USER1, "CHF", BigDecimal.valueOf(90.0), LocalDateTime.now()),
            new Portfolio(6L, USER1, "CAD", BigDecimal.valueOf(120.0), LocalDateTime.now()),
            new Portfolio(7L, USER1, "AUD", BigDecimal.valueOf(110.0), LocalDateTime.now())
        )),
        "jane.smith@example.com", new java.util.ArrayList<>(List.of(
            new Portfolio(9L, USER2, "EUR", BigDecimal.valueOf(170.0), LocalDateTime.now()),
            new Portfolio(10L, USER2, "GBP", BigDecimal.valueOf(150.0), LocalDateTime.now()),
            new Portfolio(11L, USER2, "JPY", BigDecimal.valueOf(20.0), LocalDateTime.now()),
            new Portfolio(12L, USER2, "CHF", BigDecimal.valueOf(180.0), LocalDateTime.now())
        )),
        "bob.johnson@example.com", new java.util.ArrayList<>(List.of(
            new Portfolio(16L, USER3, "EUR", BigDecimal.valueOf(42.0), LocalDateTime.now()),
            new Portfolio(17L, USER3, "GBP", BigDecimal.valueOf(37.0), LocalDateTime.now()),
            new Portfolio(18L, USER3, "JPY", BigDecimal.valueOf(50.0), LocalDateTime.now()),
            new Portfolio(19L, USER3, "CHF", BigDecimal.valueOf(45.0), LocalDateTime.now()),
            new Portfolio(21L, USER3, "AUD", BigDecimal.valueOf(55.0), LocalDateTime.now())
        ))
    );
}