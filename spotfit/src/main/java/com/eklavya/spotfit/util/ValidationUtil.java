package com.eklavya.spotfit.util;

import com.eklavya.spotfit.model.OperationResult;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for validating user credentials, emails, and passwords in SpotFit.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String sanitizeText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) {
            return false;
        }
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() >= 7 && digits.length() <= 15;
    }

    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean isValidEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        String normalized = normalizeEmail(email);
        return EMAIL_PATTERN.matcher(normalized).matches();
    }

    public static boolean isValidPassword(String password) {
        return validatePassword(password).isSuccess();
    }

    public static boolean passwordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    public static OperationResult<Void> validateEmail(String email) {
        if (isBlank(email)) {
            return OperationResult.failure("EMAIL_REQUIRED", "Email address is required.");
        }
        if (!isValidEmail(email)) {
            return OperationResult.failure("EMAIL_INVALID", "Please enter a valid email address.");
        }
        return OperationResult.success(null, "Valid email.");
    }

    public static OperationResult<Void> validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return OperationResult.failure("PASSWORD_REQUIRED", "Password is required.");
        }
        if (!password.trim().equals(password)) {
            return OperationResult.failure("PASSWORD_SURROUNDING_WHITESPACE", "Password cannot start or end with whitespace.");
        }
        if (password.length() < 8) {
            return OperationResult.failure("PASSWORD_TOO_SHORT", "Password must be at least 8 characters long.");
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }

        if (!hasLetter) {
            return OperationResult.failure("PASSWORD_LETTER_REQUIRED", "Password must contain at least one letter.");
        }
        if (!hasDigit) {
            return OperationResult.failure("PASSWORD_NUMBER_REQUIRED", "Password must contain at least one number.");
        }

        return OperationResult.success(null, "Valid password.");
    }

    public static OperationResult<Void> validateCredentials(String email, String password) {
        OperationResult<Void> emailResult = validateEmail(email);
        if (emailResult.isFailure()) {
            return emailResult;
        }
        OperationResult<Void> passwordResult = validatePassword(password);
        if (passwordResult.isFailure()) {
            return passwordResult;
        }
        return OperationResult.success(null, "Valid credentials.");
    }
}
