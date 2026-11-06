package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.config.FirebaseConfig;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.model.OperationResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Access object for Firebase Authentication REST APIs in SpotFit.
 */
public class AuthDao {

    private final HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    public AuthDao() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build());
    }

    public AuthDao(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CompletableFuture<AuthResult> signUp(String email, String password) {
        OperationResult<Void> configCheck = FirebaseConfig.validateConfiguration();
        if (configCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("FIREBASE_NOT_CONFIGURED", configCheck.getMessage()));
        }

        String apiKey = FirebaseConfig.getApiKey();
        String url = FirebaseConfig.getAuthBaseUrl() + "/accounts:signUp?key=" + apiKey;

        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("returnSecureToken", true);

        HttpRequest request = buildJsonPostRequest(url, payload);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, throwable) -> handleAuthResponse(response, throwable, false, "Account registered successfully."));
    }

    public CompletableFuture<AuthResult> signIn(String email, String password) {
        OperationResult<Void> configCheck = FirebaseConfig.validateConfiguration();
        if (configCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("FIREBASE_NOT_CONFIGURED", configCheck.getMessage()));
        }

        String apiKey = FirebaseConfig.getApiKey();
        String url = FirebaseConfig.getAuthBaseUrl() + "/accounts:signInWithPassword?key=" + apiKey;

        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("returnSecureToken", true);

        HttpRequest request = buildJsonPostRequest(url, payload);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, throwable) -> handleAuthResponse(response, throwable, true, "Signed in successfully."));
    }

    public CompletableFuture<AuthResult> getAccountInfo(String idToken) {
        OperationResult<Void> configCheck = FirebaseConfig.validateConfiguration();
        if (configCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("FIREBASE_NOT_CONFIGURED", configCheck.getMessage()));
        }

        String apiKey = FirebaseConfig.getApiKey();
        String url = FirebaseConfig.getAuthBaseUrl() + "/accounts:lookup?key=" + apiKey;

        JSONObject payload = new JSONObject();
        payload.put("idToken", idToken);

        HttpRequest request = buildJsonPostRequest(url, payload);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        return mapTransportErrorToAuthResult(throwable);
                    }
                    if (response.statusCode() >= 400) {
                        FirebaseError error = parseFirebaseError(response.statusCode(), response.body());
                        return AuthResult.failure(error.code, error.message);
                    }
                    try {
                        JSONObject json = new JSONObject(response.body());
                        if (!json.has("users")) {
                            return AuthResult.failure("AUTH_INVALID_RESPONSE", "Firebase returned an invalid user account response.");
                        }
                        JSONArray users = json.getJSONArray("users");
                        if (users.isEmpty()) {
                            return AuthResult.failure("USER_NOT_FOUND", "Account not found.");
                        }
                        JSONObject user = users.getJSONObject(0);
                        String userId = user.optString("localId", null);
                        String email = user.optString("email", null);
                        boolean emailVerified = user.optBoolean("emailVerified", false);

                        return AuthResult.success(userId, email, idToken, null, 0L, emailVerified, "Account information retrieved.");
                    } catch (Exception e) {
                        return AuthResult.failure("AUTH_INVALID_RESPONSE", "Firebase returned an invalid authentication response.");
                    }
                });
    }

    public CompletableFuture<OperationResult<Void>> sendEmailVerification(String idToken) {
        OperationResult<Void> configCheck = FirebaseConfig.validateConfiguration();
        if (configCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIREBASE_NOT_CONFIGURED", configCheck.getMessage()));
        }

        String apiKey = FirebaseConfig.getApiKey();
        String url = FirebaseConfig.getAuthBaseUrl() + "/accounts:sendOobCode?key=" + apiKey;

        JSONObject payload = new JSONObject();
        payload.put("requestType", "VERIFY_EMAIL");
        payload.put("idToken", idToken);

        HttpRequest request = buildJsonPostRequest(url, payload);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, throwable) -> handleActionResponse(response, throwable, "Verification email sent."));
    }

    public CompletableFuture<OperationResult<Void>> sendPasswordReset(String email) {
        OperationResult<Void> configCheck = FirebaseConfig.validateConfiguration();
        if (configCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIREBASE_NOT_CONFIGURED", configCheck.getMessage()));
        }

        String apiKey = FirebaseConfig.getApiKey();
        String url = FirebaseConfig.getAuthBaseUrl() + "/accounts:sendOobCode?key=" + apiKey;

        JSONObject payload = new JSONObject();
        payload.put("requestType", "PASSWORD_RESET");
        payload.put("email", email);

        HttpRequest request = buildJsonPostRequest(url, payload);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, throwable) -> handleActionResponse(response, throwable, "Password reset email sent."));
    }

    public CompletableFuture<AuthResult> refreshToken(String refreshToken) {
        OperationResult<Void> configCheck = FirebaseConfig.validateConfiguration();
        if (configCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("FIREBASE_NOT_CONFIGURED", configCheck.getMessage()));
        }

        String apiKey = FirebaseConfig.getApiKey();
        String url = FirebaseConfig.getSecureTokenBaseUrl() + "/token?key=" + apiKey;

        String encodedToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        String formBody = "grant_type=refresh_token&refresh_token=" + encodedToken;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        return mapTransportErrorToAuthResult(throwable);
                    }
                    if (response.statusCode() >= 400) {
                        FirebaseError error = parseFirebaseError(response.statusCode(), response.body());
                        return AuthResult.failure(error.code, error.message);
                    }
                    try {
                        JSONObject json = new JSONObject(response.body());
                        String newIdToken = json.optString("id_token", null);
                        String newRefreshToken = json.optString("refresh_token", null);
                        String userId = json.optString("user_id", null);
                        long expiresIn = parseLongSafely(json.optString("expires_in", "0"));

                        return AuthResult.success(userId, null, newIdToken, newRefreshToken, expiresIn, false, "Token refreshed successfully.");
                    } catch (Exception e) {
                        return AuthResult.failure("AUTH_INVALID_RESPONSE", "Firebase returned an invalid token refresh response.");
                    }
                });
    }

    private HttpRequest buildJsonPostRequest(String url, JSONObject payload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
    }

    private AuthResult handleAuthResponse(HttpResponse<String> response, Throwable throwable, boolean defaultEmailVerified, String successMsg) {
        if (throwable != null) {
            return mapTransportErrorToAuthResult(throwable);
        }
        if (response.statusCode() >= 400) {
            FirebaseError error = parseFirebaseError(response.statusCode(), response.body());
            return AuthResult.failure(error.code, error.message);
        }
        try {
            JSONObject json = new JSONObject(response.body());
            String userId = json.optString("localId", null);
            String email = json.optString("email", null);
            String idToken = json.optString("idToken", null);
            String refreshToken = json.optString("refreshToken", null);
            long expiresIn = parseLongSafely(json.optString("expiresIn", "0"));

            return AuthResult.success(userId, email, idToken, refreshToken, expiresIn, defaultEmailVerified, successMsg);
        } catch (Exception e) {
            return AuthResult.failure("AUTH_INVALID_RESPONSE", "Firebase returned an invalid authentication response.");
        }
    }

    private OperationResult<Void> handleActionResponse(HttpResponse<String> response, Throwable throwable, String successMsg) {
        if (throwable != null) {
            return mapTransportErrorToOperationResult(throwable);
        }
        if (response.statusCode() >= 400) {
            FirebaseError error = parseFirebaseError(response.statusCode(), response.body());
            return OperationResult.failure(error.code, error.message);
        }
        return OperationResult.success(null, successMsg);
    }

    private long parseLongSafely(String val) {
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private AuthResult mapTransportErrorToAuthResult(Throwable throwable) {
        Throwable cause = (throwable instanceof CompletionException && throwable.getCause() != null)
                ? throwable.getCause() : throwable;

        if (cause instanceof HttpTimeoutException) {
            return AuthResult.failure("AUTH_TIMEOUT", "Firebase authentication request timed out.");
        } else if (cause instanceof ConnectException) {
            return AuthResult.failure("AUTH_CONNECTION_FAILED", "Unable to connect to Firebase Authentication.");
        } else if (cause instanceof IOException) {
            return AuthResult.failure("AUTH_NETWORK_ERROR", "A network error occurred while contacting Firebase.");
        } else if (cause instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            return AuthResult.failure("AUTH_INTERRUPTED", "Authentication request was interrupted.");
        } else {
            return AuthResult.failure("AUTH_INVALID_RESPONSE", "Firebase returned an invalid authentication response.");
        }
    }

    private OperationResult<Void> mapTransportErrorToOperationResult(Throwable throwable) {
        Throwable cause = (throwable instanceof CompletionException && throwable.getCause() != null)
                ? throwable.getCause() : throwable;

        if (cause instanceof HttpTimeoutException) {
            return OperationResult.failure("AUTH_TIMEOUT", "Firebase authentication request timed out.");
        } else if (cause instanceof ConnectException) {
            return OperationResult.failure("AUTH_CONNECTION_FAILED", "Unable to connect to Firebase Authentication.");
        } else if (cause instanceof IOException) {
            return OperationResult.failure("AUTH_NETWORK_ERROR", "A network error occurred while contacting Firebase.");
        } else if (cause instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            return OperationResult.failure("AUTH_INTERRUPTED", "Authentication request was interrupted.");
        } else {
            return OperationResult.failure("AUTH_INVALID_RESPONSE", "Firebase returned an invalid authentication response.");
        }
    }

    private FirebaseError parseFirebaseError(int statusCode, String responseBody) {
        if (responseBody != null && !responseBody.isBlank()) {
            try {
                JSONObject json = new JSONObject(responseBody);
                if (json.has("error")) {
                    JSONObject errObj = json.getJSONObject("error");
                    String rawMessage = errObj.optString("message", "");

                    String canonicalCode = rawMessage;
                    if (canonicalCode.contains(":")) {
                        canonicalCode = canonicalCode.split(":")[0].trim();
                    }
                    if (canonicalCode.contains(" ")) {
                        canonicalCode = canonicalCode.split(" ")[0].trim();
                    }

                    String friendlyMessage = getFriendlyErrorMessage(canonicalCode);
                    return new FirebaseError(canonicalCode, friendlyMessage);
                }
            } catch (Exception ignored) {
            }
        }
        return new FirebaseError("FIREBASE_REQUEST_FAILED", "Firebase authentication request failed.");
    }

    private String getFriendlyErrorMessage(String code) {
        return switch (code) {
            case "EMAIL_EXISTS" -> "An account already exists with this email address.";
            case "OPERATION_NOT_ALLOWED" -> "Email and password authentication is not enabled for this Firebase project.";
            case "TOO_MANY_ATTEMPTS_TRY_LATER" -> "Too many attempts. Please try again later.";
            case "EMAIL_NOT_FOUND", "INVALID_PASSWORD", "INVALID_LOGIN_CREDENTIALS" -> "Incorrect email or password.";
            case "USER_DISABLED" -> "This account has been disabled.";
            case "INVALID_EMAIL" -> "Please enter a valid email address.";
            case "MISSING_PASSWORD" -> "Password is required.";
            case "WEAK_PASSWORD" -> "Please choose a stronger password.";
            case "INVALID_ID_TOKEN", "TOKEN_EXPIRED" -> "Your session has expired. Please sign in again.";
            case "USER_NOT_FOUND" -> "Account not found.";
            case "INVALID_REFRESH_TOKEN" -> "Your session cannot be refreshed. Please sign in again.";
            case "MISSING_REFRESH_TOKEN" -> "Refresh token is required.";
            case "PROJECT_NUMBER_MISMATCH" -> "Firebase project configuration mismatch.";
            case "EXPIRED_OOB_CODE" -> "Verification link has expired.";
            case "INVALID_OOB_CODE" -> "Invalid verification link.";
            default -> "Firebase authentication request failed.";
        };
    }

    private static class FirebaseError {
        private final String code;
        private final String message;

        private FirebaseError(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
