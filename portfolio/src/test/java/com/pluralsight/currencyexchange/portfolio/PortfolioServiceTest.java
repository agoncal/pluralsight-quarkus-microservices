package com.pluralsight.currencyexchange.portfolio;

import com.pluralsight.currencyexchange.portfolio.trade.Trade;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

@QuarkusTest
class PortfolioServiceTest {

    @Inject
    PortfolioService portfolioService;

    @Test
    void shouldGetUserPortfolioForExistingUser() {
        // Given - existing user
        String userId = "john.doe@example.com";

        // When
        List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);

        // Then
        assertNotNull(portfolios);
        assertFalse(portfolios.isEmpty());
        assertEquals(6, portfolios.size()); // John Doe has 6 portfolios based on User.java

        // Verify portfolio details
        Portfolio eurPortfolio = portfolios.stream()
            .filter(p -> "EUR".equals(p.currency()))
            .findFirst()
            .orElseThrow();

        assertEquals("EUR", eurPortfolio.currency());
        assertEquals(BigDecimal.valueOf(170.0), eurPortfolio.balance());
        assertEquals("John", eurPortfolio.user().name());
        assertEquals("Doe", eurPortfolio.user().surname());
        assertEquals("john.doe@example.com", eurPortfolio.user().email());

        // Verify credit card information
        assertEquals("**** **** **** 1234", eurPortfolio.user().number());
        assertEquals("VISA", eurPortfolio.user().type());
        assertNotNull(eurPortfolio.user().expiryDate());
    }

    @Test
    void shouldGetUserPortfolioForJaneSmith() {
        // Given - Jane Smith user
        String userId = "jane.smith@example.com";

        // When
        List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);

        // Then
        assertNotNull(portfolios);
        assertFalse(portfolios.isEmpty());
        assertEquals(4, portfolios.size()); // Jane Smith has 4 portfolios

        // Verify user details
        Portfolio anyPortfolio = portfolios.get(0);
        assertEquals("Jane", anyPortfolio.user().name());
        assertEquals("Smith", anyPortfolio.user().surname());
        assertEquals("jane.smith@example.com", anyPortfolio.user().email());

        // Verify Jane's credit card information
        assertEquals("**** **** **** 9876", anyPortfolio.user().number());
        assertEquals("MASTERCARD", anyPortfolio.user().type());
        assertNotNull(anyPortfolio.user().expiryDate());

        // Check specific EUR portfolio
        Portfolio eurPortfolio = portfolios.stream()
            .filter(p -> "EUR".equals(p.currency()))
            .findFirst()
            .orElseThrow();
        assertEquals(BigDecimal.valueOf(170.0), eurPortfolio.balance());
    }

    @Test
    void shouldGetUserPortfolioForBobJohnson() {
        // Given - Bob Johnson user
        String userId = "bob.johnson@example.com";

        // When
        List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);

        // Then
        assertNotNull(portfolios);
        assertFalse(portfolios.isEmpty());
        assertEquals(5, portfolios.size()); // Bob Johnson has 5 portfolios

        // Verify user details
        Portfolio anyPortfolio = portfolios.get(0);
        assertEquals("Bob", anyPortfolio.user().name());
        assertEquals("Johnson", anyPortfolio.user().surname());
        assertEquals("bob.johnson@example.com", anyPortfolio.user().email());

        // Verify Bob's credit card information
        assertEquals("**** **** **** 0005", anyPortfolio.user().number());
        assertEquals("AMEX", anyPortfolio.user().type());
        assertNotNull(anyPortfolio.user().expiryDate());

        // Check specific EUR portfolio
        Portfolio eurPortfolio = portfolios.stream()
            .filter(p -> "EUR".equals(p.currency()))
            .findFirst()
            .orElseThrow();
        assertEquals(BigDecimal.valueOf(42.0), eurPortfolio.balance());
    }

    @Test
    void shouldReturnEmptyListForNonExistentUser() {
        // Given - non-existent user
        String userId = "nonexistent@example.com";

        // When
        List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);

        // Then
        assertNotNull(portfolios);
        assertTrue(portfolios.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForEmptyUserId() {
        // Given - empty user ID
        String userId = "";

        // When
        List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);

        // Then
        assertNotNull(portfolios);
        assertTrue(portfolios.isEmpty());
    }

//    @Test
//    void shouldGetAllCurrentRates() {
//        // When - calling the real exchange rate service
//        List<ExchangeRate> rates = portfolioService.getAllCurrentRates();
//
//        // Then - verify rates are returned
//        assertNotNull(rates);
//        assertFalse(rates.isEmpty());
//        assertEquals(6, rates.size()); // Should return all supported currencies
//
//        // Verify all supported currencies are present
//        List<String> currencies = rates.stream()
//            .map(ExchangeRate::currency)
//            .sorted()
//            .toList();
//        assertEquals(List.of("AUD", "CAD", "CHF", "EUR", "GBP", "JPY"), currencies);
//
//        // Verify all rates have valid data
//        assertTrue(rates.stream()
//            .allMatch(rate -> rate.getCurrency() != null && !rate.getCurrency().isEmpty()));
//        assertTrue(rates.stream()
//            .allMatch(rate -> rate.getRate() != null && rate.getRate().compareTo(BigDecimal.ZERO) > 0));
//        assertTrue(rates.stream()
//            .allMatch(rate -> rate.timestamp() != null));
//    }
//
//    @Test
//    void shouldGetCurrentRateForSpecificCurrency() {
//        // When - getting EUR rate
//        ExchangeRate rate = portfolioService.getCurrentRate("EUR");
//
//        // Then
//        assertNotNull(rate);
//        assertEquals("EUR", rate.currency());
//        assertNotNull(rate.rate());
//        assertTrue(rate.rate().compareTo(BigDecimal.ZERO) > 0);
//        assertNotNull(rate.timestamp());
//    }
//
//    @Test
//    void shouldGetCurrentRateForAllSupportedCurrencies() {
//        // Given - all supported currencies
//        String[] currencies = {"EUR", "GBP", "JPY", "CHF", "CAD", "AUD"};
//
//        for (String currency : currencies) {
//            // When
//            ExchangeRate rate = portfolioService.getCurrentRate(currency);
//
//            // Then
//            assertNotNull(rate, "Rate should not be null for currency: " + currency);
//            assertEquals(currency, rate.currency());
//            assertNotNull(rate.rate());
//            assertTrue(rate.rate().compareTo(BigDecimal.ZERO) > 0,
//                "Rate should be positive for currency: " + currency);
//            assertNotNull(rate.timestamp());
//        }
//    }
//
//    @Test
//    void shouldHandleUnsupportedCurrency() {
//        // When - requesting unsupported currency
//        assertThrows(IllegalArgumentException.class, () -> {
//            portfolioService.getCurrentRate("INVALID");
//        });
//    }

    @Test
    void shouldVerifyAllSupportedCurrenciesInPortfolios() {
        // Given - John Doe user
        String userId = "john.doe@example.com";

        // When
        List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);

        // Then - verify all supported currencies are present
        List<String> currencies = portfolios.stream()
            .map(Portfolio::currency)
            .sorted()
            .toList();

        assertEquals(List.of("AUD", "CAD", "CHF", "EUR", "GBP", "JPY"), currencies);

        // Verify all portfolios belong to the same user
        assertTrue(portfolios.stream()
            .allMatch(p -> "john.doe@example.com".equals(p.user().email())));

        // Verify all portfolios have positive balances
        assertTrue(portfolios.stream()
            .allMatch(p -> p.balance().compareTo(BigDecimal.ZERO) > 0));

        // Verify all portfolios have non-null timestamps
        assertTrue(portfolios.stream()
            .allMatch(p -> p.lastUpdated() != null));

        // Verify all portfolios have users with credit card information
        assertTrue(portfolios.stream()
            .allMatch(p -> p.user().number() != null && p.user().type() != null && p.user().expiryDate() != null));
    }

    @Test
    void shouldVerifyPortfolioIds() {
        // Given - John Doe user
        String userId = "john.doe@example.com";

        // When
        List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);

        // Then - verify portfolio IDs are unique and not null
        List<Long> portfolioIds = portfolios.stream()
            .map(Portfolio::id)
            .toList();

        assertFalse(portfolioIds.contains(null));
        assertEquals(portfolioIds.size(), portfolioIds.stream().distinct().count()); // All IDs unique

        // Verify specific portfolio IDs match expected values from User.java
        assertTrue(portfolioIds.contains(2L)); // EUR portfolio
        assertTrue(portfolioIds.contains(3L)); // GBP portfolio
        assertTrue(portfolioIds.contains(4L)); // JPY portfolio
        assertTrue(portfolioIds.contains(5L)); // CHF portfolio
        assertTrue(portfolioIds.contains(6L)); // CAD portfolio
        assertTrue(portfolioIds.contains(7L)); // AUD portfolio
    }

    @Test
    void shouldVerifyCreditCardInformation() {
        // Test John Doe's credit card
        List<Portfolio> johnPortfolios = portfolioService.getUserPortfolio("john.doe@example.com");
        Portfolio johnPortfolio = johnPortfolios.get(0);

        assertEquals("**** **** **** 1234", johnPortfolio.user().number());
        assertEquals("VISA", johnPortfolio.user().type());
        assertNotNull(johnPortfolio.user().expiryDate());

        // Test Jane Smith's credit card
        List<Portfolio> janePortfolios = portfolioService.getUserPortfolio("jane.smith@example.com");
        Portfolio janePortfolio = janePortfolios.get(0);

        assertEquals("**** **** **** 9876", janePortfolio.user().number());
        assertEquals("MASTERCARD", janePortfolio.user().type());
        assertNotNull(janePortfolio.user().expiryDate());

        // Test Bob Johnson's credit card
        List<Portfolio> bobPortfolios = portfolioService.getUserPortfolio("bob.johnson@example.com");
        Portfolio bobPortfolio = bobPortfolios.get(0);

        assertEquals("**** **** **** 0005", bobPortfolio.user().number());
        assertEquals("AMEX", bobPortfolio.user().type());
        assertNotNull(bobPortfolio.user().expiryDate());
    }

    @Test
    void shouldVerifyCreditCardMasking() {
        // Verify that all users have masked credit card numbers
        String[] userIds = {"john.doe@example.com", "jane.smith@example.com", "bob.johnson@example.com"};

        for (String userId : userIds) {
            List<Portfolio> portfolios = portfolioService.getUserPortfolio(userId);
            if (!portfolios.isEmpty()) {
                Portfolio portfolio = portfolios.get(0);
                String cardNumber = portfolio.user().number();

                // Verify the card number is masked (starts with **** and has 4 digits at the end)
                assertTrue(cardNumber.startsWith("**** **** ****"),
                    "Card number should be masked for user: " + userId);
                assertTrue(cardNumber.matches(".*\\d{4}$"),
                    "Card number should end with 4 digits for user: " + userId);
                assertEquals(19, cardNumber.length(),
                    "Masked card number should be 19 characters long for user: " + userId);
            }
        }
    }

    @Test
    void shouldExecuteTradeSuccessfully() {
        // Given - a valid trade
        String userId = "john.doe@example.com";
        BigDecimal usdAmount = BigDecimal.valueOf(100.0);
        String toCurrency = "EUR";
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);

        Trade trade = new Trade(userId, usdAmount, toCurrency, exchangeRate);

        // When - executing the trade
        portfolioService.executeTrade(trade);

        // Then - trade should be executed and stored
        List<Trade> trades = portfolioService.getAllTrades(userId);
        assertNotNull(trades);
        assertFalse(trades.isEmpty());

        // Verify the trade was added to the user's trade history
        Trade executedTrade = trades.get(trades.size() - 1); // Get the last trade
        assertEquals(userId, executedTrade.userId());
        assertEquals(usdAmount, executedTrade.usdAmount());
        assertEquals(toCurrency, executedTrade.toCurrency());
        assertEquals(exchangeRate, executedTrade.exchangeRate());
        assertNotNull(executedTrade.convertedAmount());
        assertNotNull(executedTrade.timestamp());
    }

    @Test
    void shouldExecuteMultipleTradesForSameUser() {
        // Given - multiple trades for the same user
        String userId = "jane.smith@example.com";

        Trade trade1 = new Trade(userId, BigDecimal.valueOf(50.0), "GBP", BigDecimal.valueOf(0.80));
        Trade trade2 = new Trade(userId, BigDecimal.valueOf(75.0), "EUR", BigDecimal.valueOf(0.85));
        Trade trade3 = new Trade(userId, BigDecimal.valueOf(25.0), "JPY", BigDecimal.valueOf(150.0));

        // When - executing multiple trades
        portfolioService.executeTrade(trade1);
        portfolioService.executeTrade(trade2);
        portfolioService.executeTrade(trade3);

        // Then - all trades should be stored
        List<Trade> trades = portfolioService.getAllTrades(userId);
        assertNotNull(trades);
        assertTrue(trades.size() >= 3); // At least 3 trades for this user

        // Verify all trades belong to the correct user
        assertTrue(trades.stream().allMatch(t -> userId.equals(t.userId())));

        // Verify all trades have valid data
        assertTrue(trades.stream().allMatch(t -> t.usdAmount().compareTo(BigDecimal.ZERO) > 0));
        assertTrue(trades.stream().allMatch(t -> t.exchangeRate().compareTo(BigDecimal.ZERO) > 0));
        assertTrue(trades.stream().allMatch(t -> t.convertedAmount() != null));
        assertTrue(trades.stream().allMatch(t -> t.timestamp() != null));
    }

    @Test
    void shouldGetAllTradesForExistingUser() {
        // Given - a user with existing trades
        String userId = "bob.johnson@example.com";

        // Execute a trade first to ensure there's at least one trade
        Trade trade = new Trade(userId, BigDecimal.valueOf(200.0), "CHF", BigDecimal.valueOf(0.92));
        portfolioService.executeTrade(trade);

        // When - getting all trades for the user
        List<Trade> trades = portfolioService.getAllTrades(userId);

        // Then - trades should be returned
        assertNotNull(trades);
        assertFalse(trades.isEmpty());

        // Verify all trades belong to the correct user
        assertTrue(trades.stream().allMatch(t -> userId.equals(t.userId())));

        // Verify trade data integrity
        for (Trade t : trades) {
            assertNotNull(t.userId());
            assertNotNull(t.timestamp());
            assertNotNull(t.usdAmount());
            assertNotNull(t.toCurrency());
            assertNotNull(t.exchangeRate());
            assertTrue(t.usdAmount().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(t.exchangeRate().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    void shouldReturnEmptyListForUserWithNoTrades() {
        // Given - a user with no trade history
        String userId = "newuser@example.com";

        // When - getting all trades for the user
        List<Trade> trades = portfolioService.getAllTrades(userId);

        // Then - empty list should be returned
        assertNotNull(trades);
        assertTrue(trades.isEmpty());
    }

    @Test
    void shouldVerifyTradeAmounts() {
        // Given - a user and multiple trades
        String userId = "john.doe@example.com";

        // When - executing multiple trades
        for (int i = 0; i < 10; i++) {
            Trade testTrade = new Trade(userId, BigDecimal.valueOf(10.0 + i), "CAD", BigDecimal.valueOf(1.35));
            portfolioService.executeTrade(testTrade);
        }

        // Then - verify all trades have valid amounts
        List<Trade> trades = portfolioService.getAllTrades(userId);
        assertNotNull(trades);
        assertFalse(trades.isEmpty());

        // Verify all trades have positive amounts
        for (Trade t : trades) {
            assertTrue(t.usdAmount().compareTo(BigDecimal.ZERO) > 0,
                      "USD amount should be positive");
            assertTrue(t.exchangeRate().compareTo(BigDecimal.ZERO) > 0,
                      "Exchange rate should be positive");
            assertNotNull(t.convertedAmount(), "Converted amount should not be null");
            assertTrue(t.convertedAmount().compareTo(BigDecimal.ZERO) > 0,
                      "Converted amount should be positive");
        }
    }
}