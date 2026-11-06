package com.eklavya.spotfit.constants;

/**
 * Standard operation status code constants for SpotFit.
 * Used by OperationResult and DAOs to classify backend, network,
 * and business logic outcomes without relying on fragile message parsing.
 */
public final class OperationCode {

    public static final String SUCCESS = "SUCCESS";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String PERMISSION_DENIED = "PERMISSION_DENIED";
    public static final String AUTHENTICATION_REQUIRED = "AUTHENTICATION_REQUIRED";
    public static final String ROLE_MISMATCH = "ROLE_MISMATCH";
    public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
    public static final String NETWORK_ERROR = "NETWORK_ERROR";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String INVALID_RESPONSE = "INVALID_RESPONSE";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final String SCHEMA_VERSION_MISMATCH = "SCHEMA_VERSION_MISMATCH";
    public static final String SPACE_NOT_V2 = "SPACE_NOT_V2";
    public static final String LIFECYCLE_WRITE_DENIED = "LIFECYCLE_WRITE_DENIED";
    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";
    public static final String EXTERNAL_SERVICE_ERROR = "EXTERNAL_SERVICE_ERROR";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String INVALID_STATE = "INVALID_STATE";

    private OperationCode() {
    }
}
