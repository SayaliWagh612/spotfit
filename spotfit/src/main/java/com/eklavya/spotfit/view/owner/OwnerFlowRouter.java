package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.view.common.SelectRole;
import com.eklavya.spotfit.view.common.WelcomePage;

/**
 * Single-Stage Router for Owner view navigation.
 */
public class OwnerFlowRouter {

    public static void goToLogin() {
        AppNavigator.setScene(SignInPage.getScene(OwnerFlowRouter::goToRoleSelection));
    }

    public static void goToRegister() {
        AppNavigator.setScene(new SpaceOwnerRegistrationPage().getScene(OwnerFlowRouter::goToLogin));
    }

    public static void goToEmailVerification(String email) {
        String targetEmail = (email != null && !email.isBlank()) ? email : AppSession.getEmail();
        AppNavigator.setScene(OwnerEmailVerification.getVerificationScene(
                targetEmail,
                OwnerFlowRouter::goToProfileSetup,
                OwnerFlowRouter::goToLogin));
    }

    public static void goToEmailVerification() {
        goToEmailVerification(AppSession.getEmail());
    }

    public static void goToProfileSetup() {
        AppNavigator.setScene(new OwnerProfileSetupPage().getScene(OwnerFlowRouter::goToDashboard));
    }

    public static void goToDashboard() {
        OwnerShell.show("dashboard");
    }

    public static void goToRoleSelection() {
        SelectRole rolePage = new SelectRole();
        AppNavigator.setScene(rolePage.getChooseLoginPageScene(() -> AppNavigator.setScene(new WelcomePage().getScene())));
    }
}
