package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Payment;
import com.eklavya.spotfit.util.ValidationUtil;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.time.Duration;

public class PaymentDao {
    private static final Logger LOGGER = Logger.getLogger(PaymentDao.class.getName());
    private static final String FIREBASE_URL = "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/payments/%s";
    private final HttpClient httpClient;
    private final String projectId;

    public PaymentDao() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.projectId = com.eklavya.spotfit.config.AppConfig.getFirebaseProjectId();
    }

    public CompletableFuture<OperationResult<Void>> createPayment(Payment payment, String idToken) {
        if (ValidationUtil.isBlank(payment.getPaymentId()) || ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Payment ID and idToken are required."));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = String.format(FIREBASE_URL, projectId, payment.getPaymentId());

                JSONObject doc = new JSONObject();
                JSONObject fields = new JSONObject();

                fields.put("paymentId", new JSONObject().put("stringValue", payment.getPaymentId()));
                fields.put("bookingId", new JSONObject().put("stringValue", payment.getBookingId()));
                fields.put("customerId", new JSONObject().put("stringValue", payment.getCustomerId()));
                fields.put("ownerId", new JSONObject().put("stringValue", payment.getOwnerId()));
                fields.put("spaceId", new JSONObject().put("stringValue", payment.getSpaceId()));
                fields.put("amountPaise", new JSONObject().put("integerValue", payment.getAmountPaise()));
                fields.put("currency", new JSONObject().put("stringValue", payment.getCurrency()));
                fields.put("status", new JSONObject().put("stringValue", payment.getStatus()));
                fields.put("mode", new JSONObject().put("stringValue", payment.getMode()));
                fields.put("createdAt", new JSONObject().put("integerValue", payment.getCreatedAt()));
                fields.put("updatedAt", new JSONObject().put("integerValue", payment.getUpdatedAt()));

                if (!ValidationUtil.isBlank(payment.getProvider())) {
                    fields.put("provider", new JSONObject().put("stringValue", payment.getProvider()));
                }
                if (!ValidationUtil.isBlank(payment.getProviderOrderId())) {
                    fields.put("providerOrderId", new JSONObject().put("stringValue", payment.getProviderOrderId()));
                }

                doc.put("fields", fields);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + idToken)
                        .header("Content-Type", "application/json")
                        .method(
                                "PATCH",
                                HttpRequest.BodyPublishers.ofString(doc.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return OperationResult.success(null);
                } else if (response.statusCode() == 409) {
                    // Create by doing a PATCH if it already exists (or via ?updateMask=...)
                    // For create, if it exists, it returns 409. But we can just use patch instead
                    // if needed, but create is fine.
                    return OperationResult.failure(OperationCode.ALREADY_EXISTS, "Payment attempt already exists.");
                } else {
                    LOGGER.warning("Failed to create payment: " + response.body());
                    return OperationResult.failure(OperationCode.EXTERNAL_SERVICE_ERROR, "Failed to persist payment.");
                }
            } catch (Exception e) {
                LOGGER.severe("Exception persisting payment: " + e.getMessage());
                return OperationResult.failure(OperationCode.INTERNAL_ERROR, "Exception persisting payment.");
            }
        });
    }
}
