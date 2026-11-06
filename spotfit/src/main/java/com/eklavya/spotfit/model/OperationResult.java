package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.OperationCode;

/**
 * Encapsulates the success or failure result of a data or service operation in SpotFit.
 *
 * @param <T> the type of data returned by a successful operation
 */
public final class OperationResult<T> {

    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String message;

    private OperationResult(boolean success, T data, String errorCode, String message) {
        this.success = success;
        this.data = data;
        if (success) {
            this.errorCode = (errorCode != null && !errorCode.isBlank()) ? errorCode : OperationCode.SUCCESS;
            this.message = (message != null) ? message : "Operation completed successfully.";
        } else {
            this.errorCode = (errorCode != null && !errorCode.isBlank()) ? errorCode : OperationCode.UNKNOWN_ERROR;
            this.message = (message != null && !message.isBlank()) ? message : "Operation failed.";
        }
    }

    public static <T> OperationResult<T> success(T data) {
        return new OperationResult<>(true, data, OperationCode.SUCCESS, "Operation completed successfully.");
    }

    public static <T> OperationResult<T> success(T data, String message) {
        return new OperationResult<>(true, data, OperationCode.SUCCESS, message);
    }

    public static <T> OperationResult<T> failure(String errorCode, String message) {
        return new OperationResult<>(false, null, errorCode, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public boolean isNotFound() {
        return OperationCode.NOT_FOUND.equals(errorCode) || "FIRESTORE_DOCUMENT_NOT_FOUND".equals(errorCode);
    }

    public boolean isPermissionDenied() {
        return OperationCode.PERMISSION_DENIED.equals(errorCode) || "FIRESTORE_PERMISSION_DENIED".equals(errorCode);
    }

    public boolean isAuthenticationRequired() {
        return OperationCode.AUTHENTICATION_REQUIRED.equals(errorCode) || "FIRESTORE_UNAUTHENTICATED".equals(errorCode);
    }

    public boolean isNetworkError() {
        return OperationCode.NETWORK_ERROR.equals(errorCode) || "FIRESTORE_NETWORK_ERROR".equals(errorCode) || "FIRESTORE_UNAVAILABLE".equals(errorCode);
    }

    public T getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasData() {
        return data != null;
    }

    @Override
    public String toString() {
        return "OperationResult{success=" + success
                + ", hasData=" + hasData()
                + ", errorCode='" + errorCode + '\''
                + ", message='" + message + '\'' + '}';
    }
}
