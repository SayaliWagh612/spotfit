package com.eklavya.spotfit.service;

import com.eklavya.spotfit.dao.UserDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.User;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Business service layer for managing SpotFit Customer User profiles.
 */
public class UserService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,12}$");

    private final UserDao userDao;

    public UserService() {
        this(new UserDao());
    }

    public UserService(UserDao userDao) {
        this.userDao = (userDao != null) ? userDao : new UserDao();
    }

    public CompletableFuture<OperationResult<User>> createInitialProfile(
            String userId,
            String name,
            String email,
            String phone,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }
        if (ValidationUtil.isBlank(name)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_NAME_REQUIRED", "User name is required."));
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_EMAIL_INVALID", "A valid email address is required."));
        }

        if (phone != null && !phone.isBlank()) {
            String cleanPhone = phone.trim().replaceAll("[\\s\\-]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("USER_PHONE_INVALID", "Please provide a valid phone number."));
            }
        }

        User user = User.createNewUser(userId, name, email, phone);
        return userDao.createUser(user, idToken);
    }

    public CompletableFuture<OperationResult<User>> getUser(
            String userId,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        return userDao.getUser(userId, idToken);
    }

    public CompletableFuture<OperationResult<User>> updateBasicProfile(
            String userId,
            String name,
            String phone,
            String profileImageUrl,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }
        if (ValidationUtil.isBlank(name)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_NAME_REQUIRED", "User name is required."));
        }

        if (phone != null && !phone.isBlank()) {
            String cleanPhone = phone.trim().replaceAll("[\\s\\-]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("USER_PHONE_INVALID", "Please provide a valid phone number."));
            }
        }

        return userDao.updateBasicProfile(userId, name, phone, profileImageUrl, idToken);
    }

    public CompletableFuture<OperationResult<User>> savePersonas(
            String userId,
            List<String> personas,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }
        if (personas == null || personas.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("PERSONA_REQUIRED", "At least one persona selection is required."));
        }

        return userDao.updatePersonas(userId, personas, idToken);
    }

    public CompletableFuture<OperationResult<User>> saveInterests(
            String userId,
            List<String> interests,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }
        if (interests == null || interests.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INTEREST_REQUIRED", "At least one interest selection is required."));
        }

        return userDao.updateInterests(userId, interests, idToken);
    }

    public CompletableFuture<OperationResult<User>> saveLocationPreferences(
            String userId,
            String city,
            double preferredDistanceKm,
            long budgetPreferencePaise,
            String preferredLanguage,
            boolean notificationsEnabled,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }
        if (ValidationUtil.isBlank(city)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("CITY_REQUIRED", "City preference is required."));
        }
        if (preferredDistanceKm <= 0.0) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("DISTANCE_INVALID", "Preferred distance must be greater than zero."));
        }
        if (budgetPreferencePaise < 0) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("BUDGET_INVALID", "Budget preference cannot be negative."));
        }

        return userDao.updateLocationPreferences(
                userId, city, preferredDistanceKm, budgetPreferencePaise,
                preferredLanguage, notificationsEnabled, idToken);
    }

    public CompletableFuture<OperationResult<User>> markEmailVerified(
            String userId,
            boolean emailVerified,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        return userDao.updateEmailVerification(userId, emailVerified, idToken);
    }

    public CompletableFuture<OperationResult<User>> completeOnboarding(
            String userId,
            String idToken) {

        if (ValidationUtil.isBlank(userId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        return userDao.completeOnboarding(userId, idToken);
    }
}
