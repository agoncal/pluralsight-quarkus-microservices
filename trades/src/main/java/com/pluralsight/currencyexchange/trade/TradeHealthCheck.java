package com.pluralsight.currencyexchange.trade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class TradeHealthCheck implements HealthCheck {

    @Inject
    TradeService tradeService;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Check trades")
            .up()
            .withData("john.doe@example.com", tradeService.getAllTrades("john.doe@example.com").toString())
            .withData("jane.smith@example.com", tradeService.getAllTrades("jane.smith@example.com").toString())
            .withData("bob.johnson@example.com", tradeService.getAllTrades("bob.johnson@example.com").toString())
            .build();
    }
}
