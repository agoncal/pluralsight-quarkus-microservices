package com.pluralsight.currencyexchange.portfolio;

import com.google.protobuf.Empty;
import com.pluralsight.currencyexchange.currency.CurrencyRateServiceGrpc;
import com.pluralsight.currencyexchange.portfolio.trade.TradeProxy;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Readiness health check for the Portfolio Service.
 * Verifies that both external dependencies (Currency gRPC service and Trades REST service)
 * can be successfully invoked before marking the Portfolio service as ready.
 */
@Readiness
@ApplicationScoped
public class PortfolioReadinessCheck implements HealthCheck {

  private static final Logger LOG = Logger.getLogger(PortfolioReadinessCheck.class);

  @GrpcClient("currency")
  CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub currencyStub;

  @RestClient
  TradeProxy tradeProxy;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Portfolio Service Readiness");

    boolean currencyServiceReady = false;
    boolean tradesServiceReady = false;

    // Ping Currency Service (gRPC)
    try {
      currencyStub.getAllCurrentRates(Empty.newBuilder().build());
      currencyServiceReady = true;
    } catch (Exception e) {
      LOG.warn("Currency service ping failed: " + e.getMessage());
    }

    // Ping Trades Service (REST)
    try {
      tradeProxy.getAllTrades("readiness-check");
      tradesServiceReady = true;
    } catch (Exception e) {
      LOG.warn("Trades service ping failed: " + e.getMessage());
    }

    // Portfolio is ready only if both services are reachable
    boolean isReady = currencyServiceReady && tradesServiceReady;

    responseBuilder
      .status(isReady)
      .withData("currencyServiceReady", currencyServiceReady)
      .withData("tradesServiceReady", tradesServiceReady);

    return responseBuilder.build();
  }
}
