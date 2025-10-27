package com.pluralsight.currencyexchange.trade;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TradeService {

    private static final Logger LOG = Logger.getLogger(TradeResource.class);

    private final Map<String, List<Trade>> tradeHistory = new HashMap<>();

    public void executeTrade(Trade trade) {
        LOG.info("Execute trade: " + trade);

        // Calculate converted amount
        BigDecimal convertedAmount = trade.usdAmount().multiply(trade.exchangeRate());

        // Determine status based on exchange rate
        String status = trade.exchangeRate().compareTo(BigDecimal.ZERO) == 0 ? "PENDING" : "COMPLETED";

        // Create new trade with converted amount and status
        Trade executedTrade = new Trade(trade.userId(), trade.timestamp(), trade.usdAmount(), trade.toCurrency(), convertedAmount, trade.exchangeRate(), status);

        // Store trade in history
        tradeHistory.computeIfAbsent(trade.userId(), k -> new ArrayList<>()).add(executedTrade);
    }

    public List<Trade> getAllTrades(String userId) {
        LOG.info("Getting trade history for user: " + userId);

        List<Trade> trades = tradeHistory.getOrDefault(userId, new ArrayList<>());

        LOG.info("Returning " + trades.size() + " trades for user: " + userId);
        return trades;
    }
}
