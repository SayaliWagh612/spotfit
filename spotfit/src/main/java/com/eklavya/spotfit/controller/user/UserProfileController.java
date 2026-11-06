package com.eklavya.spotfit.controller.user;

import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.User;
import com.eklavya.spotfit.service.UserService;
import com.eklavya.spotfit.session.AppSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller managing User profile and onboarding operations for SpotFit.
 * Interacts strictly with UserService and AppSession without JavaFX dependencies.
 */
public class UserProfileController {

    private final UserService userService;

    public UserProfileController() {
        this(new UserService());
    }

    public UserProfileController(UserService userService) {
        this.userService = (userService != null) ? userService : new UserService();
    }

    public CompletableFuture<OperationResult<User>> createInitialProfile(
            String name,
            String email,
            String phone) {

        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return userService.createInitialProfile(
                AppSession.getUserId(),
                name,
                email,
                phone,
                AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<User>> loadCurrentUser() {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return userService.getUser(AppSession.getUserId(), AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<User>> savePersonas(List<String> personas) {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return userService.savePersonas(AppSession.getUserId(), personas, AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<User>> saveInterests(List<String> interests) {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return userService.saveInterests(AppSession.getUserId(), interests, AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<User>> saveLocationPreferences(
            String city,
            double preferredDistanceKm,
            long budgetPreferencePaise,
            String preferredLanguage,
            boolean notificationsEnabled) {

        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return userService.saveLocationPreferences(
                AppSession.getUserId(),
                city,
                preferredDistanceKm,
                budgetPreferencePaise,
                preferredLanguage,
                notificationsEnabled,
                AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<User>> markCurrentUserEmailVerified() {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return userService.markEmailVerified(AppSession.getUserId(), true, AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<User>> completeOnboarding() {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return userService.completeOnboarding(AppSession.getUserId(), AppSession.getIdToken());
    }
}
