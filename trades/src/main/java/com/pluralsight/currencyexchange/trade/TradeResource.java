package com.pluralsight.currencyexchange.trade;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/trades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Trades", description = "Currency exchange trade operations")
public class TradeResource {

    @Inject
    TradeService tradeService;

    @POST
    @Operation(summary = "Execute a currency trade", description = "Executes a USD-based currency exchange trade")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Trade executed successfully"),
        @APIResponse(responseCode = "400", description = "Invalid trade data")
    })
    public void executeTrade(@RequestBody(description = "Trade to execute") Trade trade) {
        tradeService.executeTrade(trade);
    }

    @GET
    @Path("/{userId}")
    @Operation(summary = "Get user trade history", description = "Retrieves all trades for a specific user")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Trade history retrieved successfully"),
        @APIResponse(responseCode = "404", description = "User not found")
    })
    public List<Trade> getAllTrades(@Parameter(description = "User ID", required = true) @PathParam("userId") String userId) {
        return tradeService.getAllTrades(userId);
    }
}
