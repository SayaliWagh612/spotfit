package com.eklavya.spotfit.model;

/**
 * Encapsulates the response from authentication operations in SpotFit.
 */
public class AuthResult {

    private boolean success;
    private String userId;
    private String email;
    private String idToken;
    private String refreshToken;
    private long expiresInSeconds;
    private boolean emailVerified;
    private String errorCode;
    private String message;

    public AuthResult() {
    }

    public AuthResult(boolean success, String userId, String email, String idToken,
                      String refreshToken, long expiresInSeconds, boolean emailVerified,
                      String errorCode, String message) {
        this.success = success;
        this.userId = userId;
        this.email = email;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
        this.emailVerified = emailVerified;
        if (success) {
            this.errorCode = errorCode;
            this.message = (message != null) ? message : "Signed in successfully.";
        } else {
            this.errorCode = (errorCode != null && !errorCode.isBlank()) ? errorCode : "AUTH_UNKNOWN_ERROR";
            this.message = (message != null && !message.isBlank()) ? message : "Authentication failed.";
        }
    }

    public static AuthResult success(String userId, String email, String idToken,
                                     String refreshToken, long expiresInSeconds,
                                     boolean emailVerified, String message) {
        return new AuthResult(true, userId, email, idToken, refreshToken,
                expiresInSeconds, emailVerified, null, message);
    }

    public static AuthResult failure(String errorCode, String message) {
        return new AuthResult(false, null, null, null, null,
                0L, false, errorCode, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean hasTokens() {
        return idToken != null && !idToken.isBlank()
                && refreshToken != null && !refreshToken.isBlank();
    }

    public boolean hasUserId() {
        return userId != null && !userId.isBlank();
    }

    @Override
    public String toString() {
        return "AuthResult{success=" + success
                + ", userIdPresent=" + hasUserId()
                + ", email='" + email + '\''
                + ", tokensPresent=" + hasTokens()
                + ", emailVerified=" + emailVerified
                + ", errorCode='" + errorCode + '\''
                + ", message='" + message + '\'' + '}';
    }
}
