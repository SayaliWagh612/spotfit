package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.User;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for User profile document operations in Cloud Firestore.
 */
public class UserDao {

    private final FirestoreRestClient firestoreClient;

    public UserDao() {
        this(new FirestoreRestClient());
    }

    public UserDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = (firestoreClient != null) ? firestoreClient : new FirestoreRestClient();
    }

    public CompletableFuture<OperationResult<User>> createUser(User user, String idToken) {
        if (user == null || user.getUserId() == null || user.getUserId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required for creation."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(user);
        return firestoreClient.setDocument(CollectionName.USERS, user.getUserId(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        User created = fromFirestoreDocument(res.getData());
                        if (created.getUserId() == null) {
                            created.setUserId(user.getUserId());
                        }
                        return OperationResult.success(created, "User profile created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<User>> getUser(String userId, String idToken) {
        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        return firestoreClient.getDocument(CollectionName.USERS, userId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        User user = fromFirestoreDocument(res.getData());
                        if (user.getUserId() == null) {
                            user.setUserId(userId);
                        }
                        return OperationResult.success(user);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<User>> updateBasicProfile(
            String userId,
            String name,
            String phone,
            String profileImageUrl,
            String idToken) {

        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("name", FirestoreDocumentUtil.stringValue(name));
        fields.put("phone", FirestoreDocumentUtil.stringValue(phone));
        fields.put("profileImageUrl", FirestoreDocumentUtil.stringValue(profileImageUrl));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("name", "phone", "profileImageUrl", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.USERS, userId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getUser(userId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<User>> updatePersonas(
            String userId,
            List<String> personas,
            String idToken) {

        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("personas", FirestoreDocumentUtil.stringArrayValue(personas));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("personas", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.USERS, userId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getUser(userId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<User>> updateInterests(
            String userId,
            List<String> interests,
            String idToken) {

        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("interests", FirestoreDocumentUtil.stringArrayValue(interests));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("interests", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.USERS, userId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getUser(userId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<User>> updateLocationPreferences(
            String userId,
            String city,
            double preferredDistanceKm,
            long budgetPreferencePaise,
            String preferredLanguage,
            boolean notificationsEnabled,
            String idToken) {

        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("city", FirestoreDocumentUtil.stringValue(city));
        fields.put("preferredDistanceKm", FirestoreDocumentUtil.doubleValue(preferredDistanceKm));
        fields.put("budgetPreferencePaise", FirestoreDocumentUtil.integerValue(budgetPreferencePaise));
        fields.put("preferredLanguage", FirestoreDocumentUtil.stringValue(preferredLanguage));
        fields.put("notificationsEnabled", FirestoreDocumentUtil.booleanValue(notificationsEnabled));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("city", "preferredDistanceKm", "budgetPreferencePaise",
                "preferredLanguage", "notificationsEnabled", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.USERS, userId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getUser(userId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<User>> updateEmailVerification(
            String userId,
            boolean emailVerified,
            String idToken) {

        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("emailVerified", FirestoreDocumentUtil.booleanValue(emailVerified));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("emailVerified", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.USERS, userId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getUser(userId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<User>> completeOnboarding(
            String userId,
            String idToken) {

        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("USER_ID_REQUIRED", "User ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("onboardingCompleted", FirestoreDocumentUtil.booleanValue(true));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("onboardingCompleted", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.USERS, userId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getUser(userId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    private JSONObject toFirestoreDocument(User user) {
        JSONObject fields = new JSONObject();
        fields.put("userId", FirestoreDocumentUtil.stringValue(user.getUserId()));
        fields.put("name", FirestoreDocumentUtil.stringValue(user.getName()));
        fields.put("email", FirestoreDocumentUtil.stringValue(user.getEmail()));
        fields.put("phone", FirestoreDocumentUtil.stringValue(user.getPhone()));
        fields.put("role", FirestoreDocumentUtil.stringValue(user.getRole()));
        fields.put("profileImageUrl", FirestoreDocumentUtil.stringValue(user.getProfileImageUrl()));
        fields.put("city", FirestoreDocumentUtil.stringValue(user.getCity()));
        fields.put("preferredLanguage", FirestoreDocumentUtil.stringValue(user.getPreferredLanguage()));
        fields.put("personas", FirestoreDocumentUtil.stringArrayValue(user.getPersonas()));
        fields.put("interests", FirestoreDocumentUtil.stringArrayValue(user.getInterests()));
        fields.put("budgetPreferencePaise", FirestoreDocumentUtil.integerValue(user.getBudgetPreferencePaise()));
        fields.put("preferredDistanceKm", FirestoreDocumentUtil.doubleValue(user.getPreferredDistanceKm()));
        fields.put("notificationsEnabled", FirestoreDocumentUtil.booleanValue(user.isNotificationsEnabled()));
        fields.put("emailVerified", FirestoreDocumentUtil.booleanValue(user.isEmailVerified()));
        fields.put("onboardingCompleted", FirestoreDocumentUtil.booleanValue(user.isOnboardingCompleted()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(user.isActive()));
        fields.put("createdAt", FirestoreDocumentUtil.integerValue(user.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(user.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    private User fromFirestoreDocument(JSONObject document) {
        JSONObject fields = FirestoreDocumentUtil.getFields(document);

        String userId = FirestoreDocumentUtil.getString(fields, "userId");
        String name = FirestoreDocumentUtil.getString(fields, "name");
        String email = FirestoreDocumentUtil.getString(fields, "email");
        String phone = FirestoreDocumentUtil.getString(fields, "phone");
        String role = FirestoreDocumentUtil.getString(fields, "role");
        String profileImageUrl = FirestoreDocumentUtil.getString(fields, "profileImageUrl");
        String city = FirestoreDocumentUtil.getString(fields, "city");
        String preferredLanguage = FirestoreDocumentUtil.getString(fields, "preferredLanguage");
        List<String> personas = FirestoreDocumentUtil.getStringList(fields, "personas");
        List<String> interests = FirestoreDocumentUtil.getStringList(fields, "interests");
        long budgetPreferencePaise = FirestoreDocumentUtil.getLong(fields, "budgetPreferencePaise", 0L);
        double preferredDistanceKm = FirestoreDocumentUtil.getDouble(fields, "preferredDistanceKm", 0.0);
        boolean notificationsEnabled = FirestoreDocumentUtil.getBoolean(fields, "notificationsEnabled", true);
        boolean emailVerified = FirestoreDocumentUtil.getBoolean(fields, "emailVerified", false);
        boolean onboardingCompleted = FirestoreDocumentUtil.getBoolean(fields, "onboardingCompleted", false);
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);
        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        return new User(
                userId, name, email, phone, role, profileImageUrl, city, preferredLanguage,
                personas, interests, budgetPreferencePaise, preferredDistanceKm,
                notificationsEnabled, emailVerified, onboardingCompleted, active,
                createdAt, updatedAt);
    }
}
