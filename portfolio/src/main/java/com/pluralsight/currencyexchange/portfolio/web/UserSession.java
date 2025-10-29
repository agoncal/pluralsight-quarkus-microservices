package com.pluralsight.currencyexchange.portfolio.web;

import com.pluralsight.currencyexchange.portfolio.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserSession {

  private User currentUser;

  public User getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  public boolean isLoggedIn() {
    return currentUser != null;
  }

  public void logout() {
    this.currentUser = null;
  }
}