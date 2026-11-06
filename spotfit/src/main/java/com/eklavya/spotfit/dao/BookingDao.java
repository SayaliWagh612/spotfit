// package com.eklavya.spotfit.dao;

// import com.eklavya.spotfit.constants.CollectionName;
// import com.eklavya.spotfit.constants.OperationCode;
// import com.eklavya.spotfit.model.Booking;
// import com.eklavya.spotfit.model.OperationResult;
// import com.eklavya.spotfit.util.FirestoreDocumentUtil;
// import org.json.JSONObject;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.CompletableFuture;

// /**
//  * DAO handling persistence and customer-constrained queries for Firestore collection /bookings/{bookingId}.
//  */
// public class BookingDao {

//     private final FirestoreRestClient firestoreClient;

//     public BookingDao() {
//         this.firestoreClient = new FirestoreRestClient();
//     }

//     public BookingDao(FirestoreRestClient firestoreClient) {
//         this.firestoreClient = (firestoreClient != null) ? firestoreClient : new FirestoreRestClient();
//     }

//     public CompletableFuture<OperationResult<Booking>> createBooking(Booking booking, String idToken) {
//         if (booking == null || booking.getBookingId() == null || booking.getBookingId().isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Booking and Booking ID are required."));
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         JSONObject doc = toFirestoreDocument(booking);
//         return firestoreClient.setDocument(CollectionName.BOOKINGS, booking.getBookingId().trim(), doc, idToken)
//                 .thenApply(res -> {
//                     if (res.isSuccess()) {
//                         return OperationResult.success(booking, "Booking created successfully.");
//                     } else {
//                         return OperationResult.failure(res.getErrorCode(), res.getMessage());
//                     }
//                 });
//     }

//     public CompletableFuture<OperationResult<Booking>> getBooking(String bookingId, String idToken) {
//         if (bookingId == null || bookingId.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Booking ID is required."));
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         return firestoreClient.getDocument(CollectionName.BOOKINGS, bookingId.trim(), idToken)
//                 .thenApply(res -> {
//                     if (res.isSuccess()) {
//                         Booking booking = fromFirestoreDocument(res.getData());
//                         if (booking != null) {
//                             return OperationResult.success(booking);
//                         } else {
//                             return OperationResult.failure(OperationCode.NOT_FOUND, "Booking not found.");
//                         }
//                     } else {
//                         return OperationResult.failure(res.getErrorCode(), res.getMessage());
//                     }
//                 });
//     }

//     public CompletableFuture<OperationResult<List<Booking>>> getBookingsForCustomer(String customerId, String idToken) {
//         if (customerId == null || customerId.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Customer ID is required."));
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         return firestoreClient.queryDocuments(CollectionName.BOOKINGS, "customerId", customerId.trim(), idToken)
//                 .thenApply(res -> {
//                     if (res.isSuccess()) {
//                         List<Booking> bookings = new ArrayList<>();
//                         if (res.getData() != null) {
//                             for (JSONObject docJson : res.getData()) {
//                                 Booking b = fromFirestoreDocument(docJson);
//                                 if (b != null) {
//                                     bookings.add(b);
//                                 }
//                             }
//                         }
//                         return OperationResult.success(bookings);
//                     } else {
//                         return OperationResult.failure(res.getErrorCode(), res.getMessage());
//                     }
//                 });
//     }

//     public CompletableFuture<OperationResult<List<Booking>>> getBookingsForOwner(String ownerId, String idToken) {
//         if (ownerId == null || ownerId.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         return firestoreClient.queryDocuments(CollectionName.BOOKINGS, "ownerId", ownerId.trim(), idToken)
//                 .thenApply(res -> {
//                     if (res.isSuccess()) {
//                         List<Booking> bookings = new ArrayList<>();
//                         if (res.getData() != null) {
//                             for (JSONObject docJson : res.getData()) {
//                                 Booking b = fromFirestoreDocument(docJson);
//                                 if (b != null) {
//                                     bookings.add(b);
//                                 }
//                             }
//                         }
//                         return OperationResult.success(bookings);
//                     } else {
//                         return OperationResult.failure(res.getErrorCode(), res.getMessage());
//                     }
//                 });
//     }

//     public CompletableFuture<OperationResult<List<Booking>>> getBookingsForManager(String spaceId, String idToken) {
//         if (spaceId == null || spaceId.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         return firestoreClient.queryDocuments(CollectionName.BOOKINGS, "spaceId", spaceId.trim(), idToken)
//                 .thenApply(res -> {
//                     if (res.isSuccess()) {
//                         List<Booking> bookings = new ArrayList<>();
//                         if (res.getData() != null) {
//                             for (JSONObject docJson : res.getData()) {
//                                 Booking b = fromFirestoreDocument(docJson);
//                                 if (b != null) {
//                                     bookings.add(b);
//                                 }
//                             }
//                         }
//                         return OperationResult.success(bookings);
//                     } else {
//                         return OperationResult.failure(res.getErrorCode(), res.getMessage());
//                     }
//                 });
//     }
    
//     public CompletableFuture<OperationResult<Booking>> updateBooking(Booking booking, String idToken) {
//         if (booking == null || booking.getBookingId() == null || booking.getBookingId().isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Booking and Booking ID are required."));
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
//         }

//         JSONObject doc = toFirestoreDocument(booking);
//         return firestoreClient.setDocument(CollectionName.BOOKINGS, booking.getBookingId().trim(), doc, idToken)
//                 .thenApply(res -> {
//                     if (res.isSuccess()) {
//                         return OperationResult.success(booking, "Booking updated successfully.");
//                     } else {
//                         return OperationResult.failure(res.getErrorCode(), res.getMessage());
//                     }
//                 });
//     }

//     public JSONObject toFirestoreDocument(Booking booking) {
//         JSONObject fields = new JSONObject();

//         fields.put("bookingId", FirestoreDocumentUtil.stringValue(booking.getBookingId()));
//         fields.put("schemaVersion", FirestoreDocumentUtil.integerValue(booking.getSchemaVersion()));
//         fields.put("customerId", FirestoreDocumentUtil.stringValue(booking.getCustomerId()));
//         fields.put("userId", FirestoreDocumentUtil.stringValue(booking.getCustomerId()));
//         fields.put("customerName", FirestoreDocumentUtil.stringValue(booking.getCustomerName()));
//         fields.put("customerEmail", FirestoreDocumentUtil.stringValue(booking.getCustomerEmail()));
//         fields.put("customerPhone", FirestoreDocumentUtil.stringValue(booking.getCustomerPhone()));
//         fields.put("spaceId", FirestoreDocumentUtil.stringValue(booking.getSpaceId()));
//         fields.put("businessId", FirestoreDocumentUtil.stringValue(booking.getBusinessId()));
//         fields.put("ownerId", FirestoreDocumentUtil.stringValue(booking.getOwnerId()));
//         fields.put("spaceName", FirestoreDocumentUtil.stringValue(booking.getSpaceName()));
//         fields.put("businessName", FirestoreDocumentUtil.stringValue(booking.getBusinessName()));
//         fields.put("bookingType", FirestoreDocumentUtil.stringValue(booking.getBookingType()));
//         fields.put("date", FirestoreDocumentUtil.stringValue(booking.getDate()));
//         fields.put("bookingDate", FirestoreDocumentUtil.stringValue(booking.getDate()));
//         fields.put("time", FirestoreDocumentUtil.stringValue(booking.getTime()));
//         fields.put("slotTime", FirestoreDocumentUtil.stringValue(booking.getTime()));
//         fields.put("startAt", FirestoreDocumentUtil.integerValue(booking.getStartAt()));
//         fields.put("endAt", FirestoreDocumentUtil.integerValue(booking.getEndAt()));
//         fields.put("participantCount", FirestoreDocumentUtil.integerValue(booking.getParticipantCount()));
//         fields.put("quantity", FirestoreDocumentUtil.integerValue(booking.getQuantity()));

//         fields.put("seatId", FirestoreDocumentUtil.stringValue(booking.getSeatId()));
//         fields.put("seatName", FirestoreDocumentUtil.stringValue(booking.getSeatName()));
//         fields.put("sessionId", FirestoreDocumentUtil.stringValue(booking.getSessionId()));
//         fields.put("deliveryAddress", FirestoreDocumentUtil.stringValue(booking.getDeliveryAddress()));
//         fields.put("requirements", FirestoreDocumentUtil.stringValue(booking.getRequirements()));
//         fields.put("requiresApproval", FirestoreDocumentUtil.booleanValue(booking.isRequiresApproval()));

//         fields.put("baseAmountPaise", FirestoreDocumentUtil.integerValue(booking.getBaseAmountPaise()));
//         fields.put("additionalAmountPaise", FirestoreDocumentUtil.integerValue(booking.getAdditionalAmountPaise()));
//         fields.put("totalAmountPaise", FirestoreDocumentUtil.integerValue(booking.getTotalAmountPaise()));
//         fields.put("quotedTotalAmountPaise", FirestoreDocumentUtil.integerValue(booking.getQuotedTotalAmountPaise()));
//         fields.put("pricingUnit", FirestoreDocumentUtil.stringValue(booking.getPricingUnit()));

//         fields.put("status", FirestoreDocumentUtil.stringValue(booking.getStatus()));
//         fields.put("paymentStatus", FirestoreDocumentUtil.stringValue(booking.getPaymentStatus()));
//         fields.put("verificationCode", FirestoreDocumentUtil.stringValue(booking.getVerificationCode()));
//         fields.put("otpCode", FirestoreDocumentUtil.stringValue(booking.getOtpCode()));

//         fields.put("createdAt", FirestoreDocumentUtil.integerValue(booking.getCreatedAt()));
//         fields.put("updatedAt", FirestoreDocumentUtil.integerValue(booking.getUpdatedAt()));

//         return FirestoreDocumentUtil.createDocument(fields);
//     }

//     public Booking fromFirestoreDocument(JSONObject doc) {
//         if (doc == null) return null;
//         JSONObject fields = FirestoreDocumentUtil.getFields(doc);
//         if (fields == null || fields.isEmpty()) return null;

//         String bookingId = FirestoreDocumentUtil.getString(fields, "bookingId");
//         if (bookingId == null || bookingId.isBlank()) {
//             // Attempt extraction from document path name
//             String name = doc.optString("name", "");
//             if (name.contains("/bookings/")) {
//                 bookingId = name.substring(name.lastIndexOf('/') + 1);
//             }
//         }
//         if (bookingId == null || bookingId.isBlank()) return null;

//         Booking booking = new Booking();
//         booking.setBookingId(bookingId);

//         int schemaVersion = FirestoreDocumentUtil.getInt(fields, "schemaVersion", 1);
//         booking.setSchemaVersion(schemaVersion);

//         String customerId = FirestoreDocumentUtil.getString(fields, "customerId");
//         if (customerId == null || customerId.isBlank()) {
//             customerId = FirestoreDocumentUtil.getString(fields, "userId");
//         }
//         booking.setCustomerId(customerId);
//         booking.setCustomerName(FirestoreDocumentUtil.getString(fields, "customerName"));
//         booking.setCustomerEmail(FirestoreDocumentUtil.getString(fields, "customerEmail"));
//         booking.setCustomerPhone(FirestoreDocumentUtil.getString(fields, "customerPhone"));

//         booking.setSpaceId(FirestoreDocumentUtil.getString(fields, "spaceId"));
//         booking.setBusinessId(FirestoreDocumentUtil.getString(fields, "businessId"));
//         booking.setOwnerId(FirestoreDocumentUtil.getString(fields, "ownerId"));
//         booking.setSpaceName(FirestoreDocumentUtil.getString(fields, "spaceName"));
//         booking.setBusinessName(FirestoreDocumentUtil.getString(fields, "businessName"));
//         booking.setBookingType(FirestoreDocumentUtil.getString(fields, "bookingType"));

//         String date = FirestoreDocumentUtil.getString(fields, "date");
//         if (date == null || date.isBlank()) {
//             date = FirestoreDocumentUtil.getString(fields, "bookingDate");
//         }
//         booking.setDate(date);

//         String time = FirestoreDocumentUtil.getString(fields, "time");
//         if (time == null || time.isBlank()) {
//             time = FirestoreDocumentUtil.getString(fields, "slotTime");
//         }
//         booking.setTime(time);

//         booking.setStartAt(FirestoreDocumentUtil.getLong(fields, "startAt", 0L));
//         booking.setEndAt(FirestoreDocumentUtil.getLong(fields, "endAt", 0L));
//         booking.setParticipantCount(FirestoreDocumentUtil.getInt(fields, "participantCount", 1));
//         booking.setQuantity(FirestoreDocumentUtil.getInt(fields, "quantity", 1));

//         booking.setSeatId(FirestoreDocumentUtil.getString(fields, "seatId"));
//         booking.setSeatName(FirestoreDocumentUtil.getString(fields, "seatName"));
//         booking.setSessionId(FirestoreDocumentUtil.getString(fields, "sessionId"));
//         booking.setDeliveryAddress(FirestoreDocumentUtil.getString(fields, "deliveryAddress"));
//         booking.setRequirements(FirestoreDocumentUtil.getString(fields, "requirements"));
//         booking.setRequiresApproval(FirestoreDocumentUtil.getBoolean(fields, "requiresApproval", false));

//         long basePaise = FirestoreDocumentUtil.getLong(fields, "baseAmountPaise", 0L);
//         long addPaise = FirestoreDocumentUtil.getLong(fields, "additionalAmountPaise", 0L);
//         long totalPaise = FirestoreDocumentUtil.getLong(fields, "totalAmountPaise", 0L);
//         long quotedPaise = FirestoreDocumentUtil.getLong(fields, "quotedTotalAmountPaise", totalPaise);

//         booking.setBaseAmountPaise(basePaise);
//         booking.setAdditionalAmountPaise(addPaise);
//         booking.setTotalAmountPaise(totalPaise);
//         booking.setQuotedTotalAmountPaise(quotedPaise);
//         booking.setPricingUnit(FirestoreDocumentUtil.getString(fields, "pricingUnit"));

//         // Fallback for V1 float amount if totalAmountPaise wasn't set
//         if (totalPaise == 0L) {
//             double v1Amount = FirestoreDocumentUtil.getDouble(fields, "amount", 0.0);
//             if (v1Amount > 0) {
//                 booking.setTotalAmountPaise((long) (v1Amount * 100));
//             }
//         }

//         booking.setStatus(FirestoreDocumentUtil.getString(fields, "status"));
//         booking.setPaymentStatus(FirestoreDocumentUtil.getString(fields, "paymentStatus"));

//         String vCode = FirestoreDocumentUtil.getString(fields, "verificationCode");
//         if (vCode == null || vCode.isBlank()) {
//             vCode = FirestoreDocumentUtil.getString(fields, "otpCode");
//         }
//         booking.setVerificationCode(vCode);

//         booking.setCreatedAt(FirestoreDocumentUtil.getLong(fields, "createdAt", 0L));
//         booking.setUpdatedAt(FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L));

//         return booking;
//     }
// }
package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.Booking;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * DAO handling persistence and customer-constrained queries for
 * Firestore collection /bookings/{bookingId}.
 */
public class BookingDao {

    private final FirestoreRestClient firestoreClient;

    public BookingDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public BookingDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = firestoreClient != null
                ? firestoreClient
                : new FirestoreRestClient();
    }

    public CompletableFuture<OperationResult<Booking>> createBooking(
            Booking booking,
            String idToken) {

        if (booking == null
                || booking.getBookingId() == null
                || booking.getBookingId().isBlank()) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Booking and Booking ID are required."));
        }

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(booking);

        return firestoreClient
                .setDocument(
                        CollectionName.BOOKINGS,
                        booking.getBookingId().trim(),
                        doc,
                        idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(
                                booking,
                                "Booking created successfully.");
                    }

                    return OperationResult.failure(
                            res.getErrorCode(),
                            res.getMessage());
                });
    }

    public CompletableFuture<OperationResult<Booking>> getBooking(
            String bookingId,
            String idToken) {

        if (bookingId == null || bookingId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Booking ID is required."));
        }

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "ID token is required."));
        }

        return firestoreClient
                .getDocument(
                        CollectionName.BOOKINGS,
                        bookingId.trim(),
                        idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Booking booking =
                                fromFirestoreDocument(res.getData());

                        if (booking != null) {
                            return OperationResult.success(booking);
                        }

                        return OperationResult.failure(
                                OperationCode.NOT_FOUND,
                                "Booking not found.");
                    }

                    return OperationResult.failure(
                            res.getErrorCode(),
                            res.getMessage());
                });
    }

    public CompletableFuture<OperationResult<List<Booking>>> getBookingsForCustomer(
            String customerId,
            String idToken) {

        if (customerId == null || customerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Customer ID is required."));
        }

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "ID token is required."));
        }

        return firestoreClient
                .queryDocuments(
                        CollectionName.BOOKINGS,
                        "customerId",
                        customerId.trim(),
                        idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Booking> bookings = new ArrayList<>();

                        if (res.getData() != null) {
                            for (JSONObject document : res.getData()) {
                                Booking booking =
                                        fromFirestoreDocument(document);

                                if (booking != null) {
                                    bookings.add(booking);
                                }
                            }
                        }

                        return OperationResult.success(bookings);
                    }

                    return OperationResult.failure(
                            res.getErrorCode(),
                            res.getMessage());
                });
    }

    public CompletableFuture<OperationResult<List<Booking>>> getBookingsForOwner(
            String ownerId,
            String idToken) {

        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Owner ID is required."));
        }

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "ID token is required."));
        }

        return firestoreClient
                .queryDocuments(
                        CollectionName.BOOKINGS,
                        "ownerId",
                        ownerId.trim(),
                        idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Booking> bookings = new ArrayList<>();

                        if (res.getData() != null) {
                            for (JSONObject document : res.getData()) {
                                Booking booking =
                                        fromFirestoreDocument(document);

                                if (booking != null) {
                                    bookings.add(booking);
                                }
                            }
                        }

                        return OperationResult.success(bookings);
                    }

                    return OperationResult.failure(
                            res.getErrorCode(),
                            res.getMessage());
                });
    }

    public CompletableFuture<OperationResult<List<Booking>>> getBookingsForManager(
            String spaceId,
            String idToken) {

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Space ID is required."));
        }

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "ID token is required."));
        }

        return firestoreClient
                .queryDocuments(
                        CollectionName.BOOKINGS,
                        "spaceId",
                        spaceId.trim(),
                        idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Booking> bookings = new ArrayList<>();

                        if (res.getData() != null) {
                            for (JSONObject document : res.getData()) {
                                Booking booking =
                                        fromFirestoreDocument(document);

                                if (booking != null) {
                                    bookings.add(booking);
                                }
                            }
                        }

                        return OperationResult.success(bookings);
                    }

                    return OperationResult.failure(
                            res.getErrorCode(),
                            res.getMessage());
                });
    }

    public CompletableFuture<OperationResult<Booking>> updateBooking(
            Booking booking,
            String idToken) {

        if (booking == null
                || booking.getBookingId() == null
                || booking.getBookingId().isBlank()) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Booking and Booking ID are required."));
        }

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(booking);

        return firestoreClient
                .setDocument(
                        CollectionName.BOOKINGS,
                        booking.getBookingId().trim(),
                        doc,
                        idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(
                                booking,
                                "Booking updated successfully.");
                    }

                    return OperationResult.failure(
                            res.getErrorCode(),
                            res.getMessage());
                });
    }

    /**
     * Temporary demo payment confirmation.
     *
     * Only the payment status, booking status and updated timestamp
     * are sent to Firestore.
     */
    public CompletableFuture<OperationResult<Booking>> confirmDemoPayment(
            Booking booking,
            String idToken) {

        if (booking == null
                || booking.getBookingId() == null
                || booking.getBookingId().isBlank()) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Booking is required."));
        }

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "Authentication is required."));
        }

        long now = System.currentTimeMillis();

        booking.setPaymentStatus("SUCCESSFUL");
        booking.setStatus("CONFIRMED");
        booking.setUpdatedAt(now);

        JSONObject fields = new JSONObject();

        fields.put(
                "paymentStatus",
                FirestoreDocumentUtil.stringValue("SUCCESSFUL"));

        fields.put(
                "status",
                FirestoreDocumentUtil.stringValue("CONFIRMED"));

        fields.put(
                "updatedAt",
                FirestoreDocumentUtil.integerValue(now));

        JSONObject document =
                FirestoreDocumentUtil.createDocument(fields);

        List<String> updateMask = List.of(
                "paymentStatus",
                "status",
                "updatedAt");

        return firestoreClient
                .updateDocument(
                        CollectionName.BOOKINGS,
                        booking.getBookingId().trim(),
                        document,
                        updateMask,
                        idToken)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return OperationResult.success(
                                booking,
                                "Demo payment approved and booking confirmed.");
                    }

                    return OperationResult.failure(
                            result.getErrorCode(),
                            result.getMessage());
                });
    }

    public JSONObject toFirestoreDocument(Booking booking) {
        JSONObject fields = new JSONObject();

        fields.put(
                "bookingId",
                FirestoreDocumentUtil.stringValue(
                        booking.getBookingId()));

        fields.put(
                "schemaVersion",
                FirestoreDocumentUtil.integerValue(
                        booking.getSchemaVersion()));

        fields.put(
                "customerId",
                FirestoreDocumentUtil.stringValue(
                        booking.getCustomerId()));

        fields.put(
                "userId",
                FirestoreDocumentUtil.stringValue(
                        booking.getCustomerId()));

        fields.put(
                "customerName",
                FirestoreDocumentUtil.stringValue(
                        booking.getCustomerName()));

        fields.put(
                "customerEmail",
                FirestoreDocumentUtil.stringValue(
                        booking.getCustomerEmail()));

        fields.put(
                "customerPhone",
                FirestoreDocumentUtil.stringValue(
                        booking.getCustomerPhone()));

        fields.put(
                "spaceId",
                FirestoreDocumentUtil.stringValue(
                        booking.getSpaceId()));

        fields.put(
                "businessId",
                FirestoreDocumentUtil.stringValue(
                        booking.getBusinessId()));

        fields.put(
                "ownerId",
                FirestoreDocumentUtil.stringValue(
                        booking.getOwnerId()));

        fields.put(
                "spaceName",
                FirestoreDocumentUtil.stringValue(
                        booking.getSpaceName()));

        fields.put(
                "businessName",
                FirestoreDocumentUtil.stringValue(
                        booking.getBusinessName()));

        fields.put(
                "bookingType",
                FirestoreDocumentUtil.stringValue(
                        booking.getBookingType()));

        fields.put(
                "date",
                FirestoreDocumentUtil.stringValue(
                        booking.getDate()));

        fields.put(
                "bookingDate",
                FirestoreDocumentUtil.stringValue(
                        booking.getDate()));

        fields.put(
                "time",
                FirestoreDocumentUtil.stringValue(
                        booking.getTime()));

        fields.put(
                "slotTime",
                FirestoreDocumentUtil.stringValue(
                        booking.getTime()));

        fields.put(
                "startAt",
                FirestoreDocumentUtil.integerValue(
                        booking.getStartAt()));

        fields.put(
                "endAt",
                FirestoreDocumentUtil.integerValue(
                        booking.getEndAt()));

        fields.put(
                "participantCount",
                FirestoreDocumentUtil.integerValue(
                        booking.getParticipantCount()));

        fields.put(
                "quantity",
                FirestoreDocumentUtil.integerValue(
                        booking.getQuantity()));

        fields.put(
                "seatId",
                FirestoreDocumentUtil.stringValue(
                        booking.getSeatId()));

        fields.put(
                "seatName",
                FirestoreDocumentUtil.stringValue(
                        booking.getSeatName()));

        fields.put(
                "sessionId",
                FirestoreDocumentUtil.stringValue(
                        booking.getSessionId()));

        fields.put(
                "deliveryAddress",
                FirestoreDocumentUtil.stringValue(
                        booking.getDeliveryAddress()));

        fields.put(
                "requirements",
                FirestoreDocumentUtil.stringValue(
                        booking.getRequirements()));

        fields.put(
                "requiresApproval",
                FirestoreDocumentUtil.booleanValue(
                        booking.isRequiresApproval()));

        fields.put(
                "baseAmountPaise",
                FirestoreDocumentUtil.integerValue(
                        booking.getBaseAmountPaise()));

        fields.put(
                "additionalAmountPaise",
                FirestoreDocumentUtil.integerValue(
                        booking.getAdditionalAmountPaise()));

        fields.put(
                "totalAmountPaise",
                FirestoreDocumentUtil.integerValue(
                        booking.getTotalAmountPaise()));

        fields.put(
                "quotedTotalAmountPaise",
                FirestoreDocumentUtil.integerValue(
                        booking.getQuotedTotalAmountPaise()));

        fields.put(
                "pricingUnit",
                FirestoreDocumentUtil.stringValue(
                        booking.getPricingUnit()));

        fields.put(
                "status",
                FirestoreDocumentUtil.stringValue(
                        booking.getStatus()));

        fields.put(
                "paymentStatus",
                FirestoreDocumentUtil.stringValue(
                        booking.getPaymentStatus()));

        fields.put(
                "verificationCode",
                FirestoreDocumentUtil.stringValue(
                        booking.getVerificationCode()));

        fields.put(
                "otpCode",
                FirestoreDocumentUtil.stringValue(
                        booking.getOtpCode()));

        fields.put(
                "createdAt",
                FirestoreDocumentUtil.integerValue(
                        booking.getCreatedAt()));

        fields.put(
                "updatedAt",
                FirestoreDocumentUtil.integerValue(
                        booking.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    public Booking fromFirestoreDocument(JSONObject doc) {
        if (doc == null) {
            return null;
        }

        JSONObject fields =
                FirestoreDocumentUtil.getFields(doc);

        if (fields == null || fields.isEmpty()) {
            return null;
        }

        String bookingId =
                FirestoreDocumentUtil.getString(
                        fields,
                        "bookingId");

        if (bookingId == null || bookingId.isBlank()) {
            String name = doc.optString("name", "");

            if (name.contains("/bookings/")) {
                bookingId =
                        name.substring(
                                name.lastIndexOf('/') + 1);
            }
        }

        if (bookingId == null || bookingId.isBlank()) {
            return null;
        }

        Booking booking = new Booking();
        booking.setBookingId(bookingId);

        int schemaVersion =
                FirestoreDocumentUtil.getInt(
                        fields,
                        "schemaVersion",
                        1);

        booking.setSchemaVersion(schemaVersion);

        String customerId =
                FirestoreDocumentUtil.getString(
                        fields,
                        "customerId");

        if (customerId == null || customerId.isBlank()) {
            customerId =
                    FirestoreDocumentUtil.getString(
                            fields,
                            "userId");
        }

        booking.setCustomerId(customerId);

        booking.setCustomerName(
                FirestoreDocumentUtil.getString(
                        fields,
                        "customerName"));

        booking.setCustomerEmail(
                FirestoreDocumentUtil.getString(
                        fields,
                        "customerEmail"));

        booking.setCustomerPhone(
                FirestoreDocumentUtil.getString(
                        fields,
                        "customerPhone"));

        booking.setSpaceId(
                FirestoreDocumentUtil.getString(
                        fields,
                        "spaceId"));

        booking.setBusinessId(
                FirestoreDocumentUtil.getString(
                        fields,
                        "businessId"));

        booking.setOwnerId(
                FirestoreDocumentUtil.getString(
                        fields,
                        "ownerId"));

        booking.setSpaceName(
                FirestoreDocumentUtil.getString(
                        fields,
                        "spaceName"));

        booking.setBusinessName(
                FirestoreDocumentUtil.getString(
                        fields,
                        "businessName"));

        booking.setBookingType(
                FirestoreDocumentUtil.getString(
                        fields,
                        "bookingType"));

        String date =
                FirestoreDocumentUtil.getString(
                        fields,
                        "date");

        if (date == null || date.isBlank()) {
            date =
                    FirestoreDocumentUtil.getString(
                            fields,
                            "bookingDate");
        }

        booking.setDate(date);

        String time =
                FirestoreDocumentUtil.getString(
                        fields,
                        "time");

        if (time == null || time.isBlank()) {
            time =
                    FirestoreDocumentUtil.getString(
                            fields,
                            "slotTime");
        }

        booking.setTime(time);

        booking.setStartAt(
                FirestoreDocumentUtil.getLong(
                        fields,
                        "startAt",
                        0L));

        booking.setEndAt(
                FirestoreDocumentUtil.getLong(
                        fields,
                        "endAt",
                        0L));

        booking.setParticipantCount(
                FirestoreDocumentUtil.getInt(
                        fields,
                        "participantCount",
                        1));

        booking.setQuantity(
                FirestoreDocumentUtil.getInt(
                        fields,
                        "quantity",
                        1));

        booking.setSeatId(
                FirestoreDocumentUtil.getString(
                        fields,
                        "seatId"));

        booking.setSeatName(
                FirestoreDocumentUtil.getString(
                        fields,
                        "seatName"));

        booking.setSessionId(
                FirestoreDocumentUtil.getString(
                        fields,
                        "sessionId"));

        booking.setDeliveryAddress(
                FirestoreDocumentUtil.getString(
                        fields,
                        "deliveryAddress"));

        booking.setRequirements(
                FirestoreDocumentUtil.getString(
                        fields,
                        "requirements"));

        booking.setRequiresApproval(
                FirestoreDocumentUtil.getBoolean(
                        fields,
                        "requiresApproval",
                        false));

        long basePaise =
                FirestoreDocumentUtil.getLong(
                        fields,
                        "baseAmountPaise",
                        0L);

        long additionalPaise =
                FirestoreDocumentUtil.getLong(
                        fields,
                        "additionalAmountPaise",
                        0L);

        long totalPaise =
                FirestoreDocumentUtil.getLong(
                        fields,
                        "totalAmountPaise",
                        0L);

        long quotedPaise =
                FirestoreDocumentUtil.getLong(
                        fields,
                        "quotedTotalAmountPaise",
                        totalPaise);

        booking.setBaseAmountPaise(basePaise);
        booking.setAdditionalAmountPaise(additionalPaise);
        booking.setTotalAmountPaise(totalPaise);
        booking.setQuotedTotalAmountPaise(quotedPaise);

        booking.setPricingUnit(
                FirestoreDocumentUtil.getString(
                        fields,
                        "pricingUnit"));

        if (totalPaise == 0L) {
            double oldAmount =
                    FirestoreDocumentUtil.getDouble(
                            fields,
                            "amount",
                            0.0);

            if (oldAmount > 0) {
                booking.setTotalAmountPaise(
                        Math.round(oldAmount * 100));
            }
        }

        booking.setStatus(
                FirestoreDocumentUtil.getString(
                        fields,
                        "status"));

        booking.setPaymentStatus(
                FirestoreDocumentUtil.getString(
                        fields,
                        "paymentStatus"));

        String verificationCode =
                FirestoreDocumentUtil.getString(
                        fields,
                        "verificationCode");

        if (verificationCode == null
                || verificationCode.isBlank()) {

            verificationCode =
                    FirestoreDocumentUtil.getString(
                            fields,
                            "otpCode");
        }

        booking.setVerificationCode(verificationCode);

        booking.setCreatedAt(
                FirestoreDocumentUtil.getLong(
                        fields,
                        "createdAt",
                        0L));

        booking.setUpdatedAt(
                FirestoreDocumentUtil.getLong(
                        fields,
                        "updatedAt",
                        0L));

        return booking;
    }
}