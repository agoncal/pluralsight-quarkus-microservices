package com.pluralsight.currencyexchange.trade;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@QuarkusTest
class TradeResourceTest {

  @Test
  void shouldExecuteAndGetTrades() {
    // First, execute a trade
    Trade trade = new Trade("user456", BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(0.92));

    given()
      .contentType(ContentType.JSON)
      .body(trade)
      .when()
      .post("/api/trades")
      .then()
      .statusCode(204);

    // Then retrieve trades for the user
    given()
      .when()
      .get("/api/trades/user456")
      .then()
      .statusCode(200)
      .body("size()", greaterThan(0))
      .body("[0].userId", is("user456"))
      .body("[0].toCurrency", is("EUR"))
      .body("[0].usdAmount", is(100))
      .body("[0].exchangeRate", is(0.92f))
      .body("[0].convertedAmount", is(92.00f))
      .body("[0].status", is("COMPLETED"))
      .body("[0].timestamp", is(org.hamcrest.Matchers.notNullValue()));
  }
}
