package com.pluralsight.currencyexchange.portfolio.web;

import com.pluralsight.currencyexchange.currency.CurrencyRate;
import com.pluralsight.currencyexchange.portfolio.Portfolio;
import com.pluralsight.currencyexchange.portfolio.PortfolioService;
import com.pluralsight.currencyexchange.portfolio.User;
import com.pluralsight.currencyexchange.portfolio.trade.Trade;
import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;

import java.math.BigDecimal;
import java.util.List;

public class WebApplication extends Controller {

  private static final Logger LOG = Logger.getLogger(WebApplication.class);

  @Inject
  UserSession userSession;

  @Inject
  PortfolioService portfolioService;

  @CheckedTemplate
  static class Templates {
    public static native TemplateInstance index();

    public static native TemplateInstance signin(String loginError, String passwordError, String email);

    public static native TemplateInstance portfolio(User user, List<Portfolio> portfolios, List<CurrencyRate> exchangeRates, List<Trade> trades);

    public static native TemplateInstance profile(User user);
  }

  @Path("/")
  public TemplateInstance index() {
    LOG.info("Entering index()");
    return Templates.index();
  }

  @GET
  @Path("/signin")
  public TemplateInstance signinPage() {
    LOG.info("Entering signinPage()");
    return Templates.signin(null, null, null);
  }

  @POST
  @Path("/signin")
  public TemplateInstance signin(@RestForm String email, @RestForm String password) {
    LOG.info("Entering signin() for user: " + (email != null ? email.trim() : "null"));
    String loginError = null;
    String passwordError = null;

    if (email == null || email.trim().isEmpty()) {
      loginError = "Email is required";
      LOG.warn("Signin failed: Email is required");
    }
    if (password == null || password.trim().isEmpty()) {
      passwordError = "Password is required";
      LOG.warn("Signin failed: Password is required");
    }

    if (loginError != null || passwordError != null) {
      return Templates.signin(loginError, passwordError, email);
    }

    User user = findUserByEmail(email.trim());
    if (user == null) {
      loginError = "User not found";
      LOG.warn("Signin failed: User not found - " + email.trim());
      return Templates.signin(loginError, passwordError, email);
    }

    if (!"password".equals(password)) {
      passwordError = "Invalid password";
      LOG.warn("Signin failed: Invalid password for user - " + email.trim());
      return Templates.signin(loginError, passwordError, email);
    }

    userSession.setCurrentUser(user);
    LOG.info("Successful signin for user: " + user.email());

    return portfolio();
  }

  @Path("/logout")
  public TemplateInstance logout() {
    LOG.info("Entering logout()");
    User currentUser = userSession.getCurrentUser();
    if (currentUser != null) {
      LOG.info("User logout: " + currentUser.email());
    }
    userSession.logout();
    return index();
  }

  @Path("/portfolio")
  public TemplateInstance portfolio() {
    LOG.info("Entering portfolio()");
    if (!userSession.isLoggedIn()) {
      LOG.info("Portfolio access attempt without authentication - redirecting to signin");
      return signinPage();
    }

    User currentUser = userSession.getCurrentUser();
    List<Portfolio> portfolios = portfolioService.getUserPortfolio(currentUser.email());
    List<CurrencyRate> exchangeRates = portfolioService.getAllCurrentRates();
    List<Trade> trades = portfolioService.getAllTrades(currentUser.email());
    LOG.info("Viewing portfolio for user: " + currentUser.email() + " with " + portfolios.size() + " entries and " + trades.size() + " trades");

    return Templates.portfolio(currentUser, portfolios, exchangeRates, trades);
  }

  @POST
  @Path("/refresh")
  public TemplateInstance refreshExchangeRates() {
    LOG.info("Entering refreshExchangeRates()");
    if (!userSession.isLoggedIn()) {
      LOG.info("Portfolio refresh attempt without authentication - redirecting to signin");
      return signinPage();
    }

    User currentUser = userSession.getCurrentUser();
    List<Portfolio> portfolios = portfolioService.getUserPortfolio(currentUser.email());
    List<CurrencyRate> exchangeRates = portfolioService.getAllCurrentRates();
    List<Trade> trades = portfolioService.getAllTrades(currentUser.email());
    LOG.info("Refreshing portfolio for user: " + currentUser.email() + " with updated exchange rates");

    return Templates.portfolio(currentUser, portfolios, exchangeRates, trades);
  }

  @Path("/profile")
  public TemplateInstance profile() {
    LOG.info("Entering profile()");
    if (!userSession.isLoggedIn()) {
      LOG.info("Profile access attempt without authentication - redirecting to signin");
      return signinPage();
    }
    User currentUser = userSession.getCurrentUser();
    LOG.info("Viewing profile for user: " + currentUser.email());
    return Templates.profile(currentUser);
  }

  @POST
  @Path("/executeTrade")
  public TemplateInstance executeTrade(@RestForm BigDecimal usdAmount, @RestForm String toCurrency) {
    LOG.info("Entering executeTrade() with amount: " + usdAmount + " to currency: " + toCurrency);

    if (!userSession.isLoggedIn()) {
      LOG.info("Trade execution attempt without authentication - redirecting to signin");
      return signinPage();
    }

    User currentUser = userSession.getCurrentUser();

    try {
      // Get current exchange rate for the target currency
      CurrencyRate exchangeRate = portfolioService.getCurrentRate(toCurrency);

      // Create and execute trade
      Trade trade = new Trade(currentUser.email(), usdAmount, toCurrency, exchangeRate.getRate());
      portfolioService.executeTrade(trade);

      LOG.info("Trade executed successfully for user: " + currentUser.email() +
        ", amount: " + usdAmount + ", currency: " + toCurrency);

    } catch (Exception e) {
      LOG.error("Trade execution failed for user: " + currentUser.email(), e);
    }

    // Redirect back to portfolio page to show updated data
    return portfolio();
  }

  private User findUserByEmail(String email) {
    return switch (email) {
      case "john.doe@example.com" -> new User(1L, "John", "Doe", "john.doe@example.com",
        "4532123456781234", java.time.YearMonth.of(2026, 12), "VISA");
      case "jane.smith@example.com" -> new User(2L, "Jane", "Smith", "jane.smith@example.com",
        "5555123456789876", java.time.YearMonth.of(2025, 8), "MASTERCARD");
      case "bob.johnson@example.com" -> new User(3L, "Bob", "Johnson", "bob.johnson@example.com",
        "378282246310005", java.time.YearMonth.of(2027, 3), "AMEX");
      default -> null;
    };
  }
}