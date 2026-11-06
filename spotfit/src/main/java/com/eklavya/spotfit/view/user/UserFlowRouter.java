package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.model.User;
import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.view.auth.EmailVerification;
import com.eklavya.spotfit.view.auth.InterestSelection;
import com.eklavya.spotfit.view.auth.LocationPreferences;
import com.eklavya.spotfit.view.auth.Login;
import com.eklavya.spotfit.view.auth.OnboardingComplete;
import com.eklavya.spotfit.view.auth.PersonaSelection;
import com.eklavya.spotfit.view.auth.Register;

/**
 * Entry point / navigation chain for the Customer (User) side of the app.
 */
public class UserFlowRouter {

    public static void goToLogin() {
        AppNavigator.setScene(
                Login.getLoginScene(UserFlowRouter::goToDash, UserFlowRouter::goToRegister));
    }

    public static void goToRegister() {
        AppNavigator.setScene(Register.getRegisterScene(
                () -> goToEmailVerification(AppSession.getEmail()),
                UserFlowRouter::goToLogin));
    }

    public static void goToEmailVerification(String email) {
        String targetEmail = (email != null && !email.isBlank()) ? email : AppSession.getEmail();
        AppNavigator.setScene(
                EmailVerification.getVerificationScene(targetEmail, UserFlowRouter::goToPersonaSelection,
                        UserFlowRouter::goToLogin));
    }

    public static void goToPersonaSelection() {
        AppNavigator.setScene(PersonaSelection.getPersonaScene(
                () -> goToEmailVerification(AppSession.getEmail()),
                UserFlowRouter::goToInterestSelection));
    }

    public static void goToInterestSelection() {
        AppNavigator.setScene(InterestSelection.getInterestScene(
                UserFlowRouter::goToPersonaSelection,
                UserFlowRouter::goToLocationPreferences));
    }

    public static void goToLocationPreferences() {
        AppNavigator.setScene(LocationPreferences.getLocationScene(
                UserFlowRouter::goToInterestSelection,
                user -> goToOnboardingComplete(user)));
    }

    public static void goToOnboardingComplete(User user) {
        AppNavigator.setScene(
                OnboardingComplete.getCompleteScene(user, UserFlowRouter::goToDash,
                        UserFlowRouter::goToPersonaSelection));
    }

    public static void goToOnboardingComplete() {
        goToOnboardingComplete((User) null);
    }

    public static void goToDash() {
        AppNavigator.setScene(UserDash.getUserDashScene());
    }
}
