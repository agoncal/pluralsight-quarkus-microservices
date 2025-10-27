package com.pluralsight.currencyexchange.portfolio.trade;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/api/trades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "trades")
public interface TradeService {

    @POST
    void executeTrade(Trade trade);

    @GET
    @Path("/{userId}")
    List<Trade> getAllTrades(@PathParam("userId") String userId);
}
