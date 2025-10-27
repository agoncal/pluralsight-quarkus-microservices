package com.pluralsight.currencyexchange.portfolio.web;

import com.pluralsight.currencyexchange.portfolio.User;
import io.quarkus.arc.Arc;
import io.quarkus.qute.TemplateGlobal;

public class TemplateGlobals {

    @TemplateGlobal
    public static User user() {
        UserSession userSession = Arc.container().instance(UserSession.class).get();
        return userSession.getCurrentUser();
    }

    @TemplateGlobal
    public static boolean isLoggedIn() {
        UserSession userSession = Arc.container().instance(UserSession.class).get();
        return userSession.isLoggedIn();
    }
}