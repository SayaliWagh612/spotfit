package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.view.common.WelcomePage;
import com.eklavya.spotfit.view.spaceManager.Dashboard;
import com.eklavya.spotfit.view.spaceManager.LoginPage;
import com.eklavya.spotfit.view.spaceManager.Registration;
import com.eklavya.spotfit.view.spaceManager.VerifyInvite;
import javafx.stage.Stage;

/**
 * Router managing navigation flows for the Space Manager module while preserving a single Stage.
 */
public class ManagerFlowRouter {

    public static void navigateToLogin(Stage stage) {
        if (stage == null) return;
        LoginPage loginPage = new LoginPage();
        AppNavigator.setScene(loginPage.getLoginPageScene(() -> AppNavigator.setScene(new WelcomePage().getScene())));
        AppNavigator.setMaximized(true);
    }

    public static void navigateToRegister(Stage stage) {
        if (stage == null) return;
        Registration registration = new Registration();
        AppNavigator.setScene(registration.getRegistrationPageScene(() -> AppNavigator.setScene(new WelcomePage().getScene())));
        AppNavigator.setMaximized(true);
    }

    public static void navigateToVerifyInvite(Stage stage) {
        if (stage == null) return;
        VerifyInvite verifyInvite = new VerifyInvite();
        AppNavigator.setScene(verifyInvite.getVerifyPageScene(
                null, "", "Manager", "", "manager@example.com",
                () -> navigateToRegister(stage),
                () -> AppNavigator.setScene(new WelcomePage().getScene())
        ));
        AppNavigator.setMaximized(true);
    }

    public static void navigateToDashboard(Stage stage, String managerName) {
        if (stage == null) return;
        Dashboard dashboard = new Dashboard();
        AppNavigator.setScene(dashboard.getDashboardPageScene(
                (managerName != null && !managerName.isBlank()) ? managerName : "Space Manager",
                () -> navigateToLogin(stage)
        ));
        AppNavigator.setMaximized(true);
    }
}
