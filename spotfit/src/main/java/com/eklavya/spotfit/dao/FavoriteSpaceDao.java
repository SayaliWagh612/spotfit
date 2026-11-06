package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FavoriteSpaceDao {

    private final FirestoreRestClient restClient;

    public FavoriteSpaceDao() {
        this.restClient = new FirestoreRestClient();
    }

    public FavoriteSpaceDao(FirestoreRestClient restClient) {
        this.restClient = restClient;
    }

    public CompletableFuture<OperationResult<Void>> addFavorite(String userId, String spaceId, long createdAt,
            String idToken) {
        List<String> segments = List.of(CollectionName.USERS, userId, "favorites", spaceId);
        JSONObject doc = new JSONObject();
        doc.put("fields", new JSONObject()
                .put("userId", FirestoreDocumentUtil.stringValue(userId))
                .put("spaceId", FirestoreDocumentUtil.stringValue(spaceId))
                .put("createdAt", FirestoreDocumentUtil.integerValue(createdAt)));

        return restClient.setDocumentBySegments(segments, doc, idToken).thenApply(res -> {
            if (res.isSuccess()) {
                return OperationResult.success(null, "Favorite added successfully");
            }
            return OperationResult.failure(res.getErrorCode(), res.getMessage());
        });
    }

    public CompletableFuture<OperationResult<Void>> removeFavorite(String userId, String spaceId, String idToken) {
        List<String> segments = List.of(CollectionName.USERS, userId, "favorites", spaceId);
        return restClient.deleteDocumentBySegments(segments, idToken);
    }

    public CompletableFuture<OperationResult<Boolean>> isFavorite(String userId, String spaceId, String idToken) {
        List<String> segments = List.of(CollectionName.USERS, userId, "favorites", spaceId);
        return restClient.getDocumentBySegments(segments, idToken).thenApply(res -> {
            if (res.isSuccess()) {
                return OperationResult.success(true);
            } else if ("NOT_FOUND".equals(res.getErrorCode())) {
                return OperationResult.success(false);
            }
            return OperationResult.failure(res.getErrorCode(), res.getMessage());
        });
    }

    public CompletableFuture<OperationResult<List<String>>> getFavoriteSpaceIds(
            String userId,
            String idToken) {

        List<String> segments = List.of(
                CollectionName.USERS,
                userId,
                "favorites");

        return restClient.listDocumentsBySegments(segments, idToken)
                .thenApply(result -> {
                    if (result.isFailure()) {
                        return OperationResult.failure(
                                result.getErrorCode(),
                                result.getMessage());
                    }

                    List<JSONObject> documents = result.getData();

                    if (documents == null || documents.isEmpty()) {
                        return OperationResult.success(List.of());
                    }

                    List<String> spaceIds = documents.stream()
                            .sorted((first, second) -> {
                                JSONObject firstFields = getDocumentFields(first);
                                JSONObject secondFields = getDocumentFields(second);

                                long firstCreatedAt = FirestoreDocumentUtil.getLong(
                                        firstFields,
                                        "createdAt",
                                        0L);

                                long secondCreatedAt = FirestoreDocumentUtil.getLong(
                                        secondFields,
                                        "createdAt",
                                        0L);

                                return Long.compare(
                                        secondCreatedAt,
                                        firstCreatedAt);
                            })
                            .map(FavoriteSpaceDao::getDocumentFields)
                            .map(fields -> FirestoreDocumentUtil.getString(
                                    fields,
                                    "spaceId"))
                            .filter(spaceId -> spaceId != null && !spaceId.isBlank())
                            .distinct()
                            .toList();

                    return OperationResult.success(spaceIds);
                });
    }

    private static JSONObject getDocumentFields(JSONObject document) {
        if (document == null) {
            return new JSONObject();
        }

        JSONObject fields = document.optJSONObject("fields");
        return fields != null ? fields : document;
    }
}
