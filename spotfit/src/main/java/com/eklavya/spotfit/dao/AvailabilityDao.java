package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.AvailabilityBlock;
import com.eklavya.spotfit.model.AvailabilityRule;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for nested Availability Rules and Availability Blocks.
 * Hierarchy: /spaces/{spaceId}/resources/{resourceId}/availabilityRules/{ruleId}
 *            /spaces/{spaceId}/resources/{resourceId}/availabilityBlocks/{blockId}
 */
public class AvailabilityDao {

    private final FirestoreRestClient firestoreClient;

    public AvailabilityDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public AvailabilityDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = firestoreClient;
    }

    // --- Availability Rules ---

    public CompletableFuture<OperationResult<AvailabilityRule>> setAvailabilityRule(
            String spaceId,
            String resourceId,
            AvailabilityRule rule,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || resourceId == null || resourceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Resource ID are required."));
        }
        if (rule == null || rule.getRuleId() == null || rule.getRuleId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Rule and Rule ID are required."));
        }
        if (!spaceId.equals(rule.getSpaceId()) || !resourceId.equals(rule.getResourceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path parameters must match AvailabilityRule model IDs."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.RESOURCES, resourceId.trim(),
                CollectionName.AVAILABILITY_RULES, rule.getRuleId().trim()
        );

        JSONObject doc = ruleToFirestoreDocument(rule);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(rule, "Availability rule saved successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<AvailabilityRule>> getAvailabilityRule(
            String spaceId,
            String resourceId,
            String dayOfWeek,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || resourceId == null || resourceId.isBlank() || dayOfWeek == null || dayOfWeek.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID, Resource ID, and Day of Week are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.RESOURCES, resourceId.trim(),
                CollectionName.AVAILABILITY_RULES, dayOfWeek.trim().toUpperCase()
        );

        return firestoreClient.getDocumentBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        AvailabilityRule rule = ruleFromFirestoreDocument(res.getData());
                        if (rule != null) {
                            return OperationResult.success(rule);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Availability rule not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<AvailabilityRule>>> getAvailabilityRules(
            String spaceId,
            String resourceId,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || resourceId == null || resourceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Resource ID are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.RESOURCES, resourceId.trim(),
                CollectionName.AVAILABILITY_RULES
        );

        return firestoreClient.listDocumentsBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<AvailabilityRule> rules = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                AvailabilityRule r = ruleFromFirestoreDocument(doc);
                                if (r != null) {
                                    rules.add(r);
                                }
                            }
                        }
                        return OperationResult.success(rules);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    // --- Availability Blocks ---

    public CompletableFuture<OperationResult<AvailabilityBlock>> createAvailabilityBlock(
            String spaceId,
            String resourceId,
            AvailabilityBlock block,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || resourceId == null || resourceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Resource ID are required."));
        }
        if (block == null || block.getBlockId() == null || block.getBlockId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Block and Block ID are required."));
        }
        if (!spaceId.equals(block.getSpaceId()) || !resourceId.equals(block.getResourceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path parameters must match AvailabilityBlock model IDs."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.RESOURCES, resourceId.trim(),
                CollectionName.AVAILABILITY_BLOCKS, block.getBlockId().trim()
        );

        JSONObject doc = blockToFirestoreDocument(block);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(block, "Availability block created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> getAvailabilityBlock(
            String spaceId,
            String resourceId,
            String blockId,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || resourceId == null || resourceId.isBlank() || blockId == null || blockId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID, Resource ID, and Block ID are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.RESOURCES, resourceId.trim(),
                CollectionName.AVAILABILITY_BLOCKS, blockId.trim()
        );

        return firestoreClient.getDocumentBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        AvailabilityBlock block = blockFromFirestoreDocument(res.getData());
                        if (block != null) {
                            return OperationResult.success(block);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Availability block not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<AvailabilityBlock>>> getAvailabilityBlocks(
            String spaceId,
            String resourceId,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || resourceId == null || resourceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Resource ID are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.RESOURCES, resourceId.trim(),
                CollectionName.AVAILABILITY_BLOCKS
        );

        return firestoreClient.listDocumentsBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<AvailabilityBlock> blocks = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                AvailabilityBlock b = blockFromFirestoreDocument(doc);
                                if (b != null) {
                                    blocks.add(b);
                                }
                            }
                        }
                        return OperationResult.success(blocks);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> updateAvailabilityBlock(
            String spaceId,
            String resourceId,
            AvailabilityBlock block,
            String idToken) {

        return createAvailabilityBlock(spaceId, resourceId, block, idToken);
    }

    // --- Additive V2 Direct Space Availability Rules ---

    public CompletableFuture<OperationResult<AvailabilityRule>> setSpaceAvailabilityRule(
            String spaceId,
            AvailabilityRule rule,
            String idToken) {

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (rule == null || rule.getRuleId() == null || rule.getRuleId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Rule and Rule ID are required."));
        }
        if (!spaceId.equals(rule.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path parameters must match AvailabilityRule model space ID."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.AVAILABILITY_RULES, rule.getRuleId().trim().toUpperCase()
        );

        rule.setResourceId(""); // Enforce V2 resourceId == ""
        JSONObject doc = ruleToFirestoreDocument(rule);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(rule, "Space availability rule saved successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<AvailabilityRule>> getSpaceAvailabilityRule(
            String spaceId,
            String dayOfWeek,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || dayOfWeek == null || dayOfWeek.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Day of Week are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.AVAILABILITY_RULES, dayOfWeek.trim().toUpperCase()
        );

        return firestoreClient.getDocumentBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        AvailabilityRule rule = ruleFromFirestoreDocument(res.getData());
                        if (rule != null) {
                            return OperationResult.success(rule);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Space availability rule not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<AvailabilityRule>>> getSpaceAvailabilityRules(
            String spaceId,
            String idToken) {

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.AVAILABILITY_RULES
        );

        return firestoreClient.listDocumentsBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<AvailabilityRule> rules = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                AvailabilityRule r = ruleFromFirestoreDocument(doc);
                                if (r != null) {
                                    rules.add(r);
                                }
                            }
                        }
                        return OperationResult.success(rules);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    // --- Additive V2 Direct Space Availability Blocks ---

    public CompletableFuture<OperationResult<AvailabilityBlock>> createSpaceAvailabilityBlock(
            String spaceId,
            AvailabilityBlock block,
            String idToken) {

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (block == null || block.getBlockId() == null || block.getBlockId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Block and Block ID are required."));
        }
        if (!spaceId.equals(block.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path parameters must match AvailabilityBlock model space ID."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.AVAILABILITY_BLOCKS, block.getBlockId().trim()
        );

        block.setResourceId(""); // Enforce V2 resourceId == ""
        JSONObject doc = blockToFirestoreDocument(block);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(block, "Space availability block created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> getSpaceAvailabilityBlock(
            String spaceId,
            String blockId,
            String idToken) {

        if (spaceId == null || spaceId.isBlank() || blockId == null || blockId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Block ID are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.AVAILABILITY_BLOCKS, blockId.trim()
        );

        return firestoreClient.getDocumentBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        AvailabilityBlock block = blockFromFirestoreDocument(res.getData());
                        if (block != null) {
                            return OperationResult.success(block);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Space availability block not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<AvailabilityBlock>>> getSpaceAvailabilityBlocks(
            String spaceId,
            String idToken) {

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.AVAILABILITY_BLOCKS
        );

        return firestoreClient.listDocumentsBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<AvailabilityBlock> blocks = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                AvailabilityBlock b = blockFromFirestoreDocument(doc);
                                if (b != null) {
                                    blocks.add(b);
                                }
                            }
                        }
                        return OperationResult.success(blocks);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> updateSpaceAvailabilityBlock(
            String spaceId,
            AvailabilityBlock block,
            String idToken) {

        return createSpaceAvailabilityBlock(spaceId, block, idToken);
    }

    // --- JSON Conversion Helpers ---

    private JSONObject ruleToFirestoreDocument(AvailabilityRule rule) {
        JSONObject fields = new JSONObject();

        fields.put("ruleId", FirestoreDocumentUtil.stringValue(rule.getRuleId()));
        fields.put("spaceId", FirestoreDocumentUtil.stringValue(rule.getSpaceId()));
        fields.put("resourceId", FirestoreDocumentUtil.stringValue(rule.getResourceId()));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(rule.getOwnerId()));
        fields.put("dayOfWeek", FirestoreDocumentUtil.stringValue(rule.getDayOfWeek()));
        fields.put("startTime", FirestoreDocumentUtil.stringValue(rule.getStartTime()));
        fields.put("endTime", FirestoreDocumentUtil.stringValue(rule.getEndTime()));
        fields.put("slotIntervalMinutes", FirestoreDocumentUtil.integerValue(rule.getSlotIntervalMinutes()));
        fields.put("minimumBookingMinutes", FirestoreDocumentUtil.integerValue(rule.getMinimumBookingMinutes()));
        fields.put("maximumBookingMinutes", FirestoreDocumentUtil.integerValue(rule.getMaximumBookingMinutes()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(rule.isActive()));
        fields.put("createdAt", FirestoreDocumentUtil.integerValue(rule.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(rule.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    private AvailabilityRule ruleFromFirestoreDocument(JSONObject document) {
        if (document == null) {
            return null;
        }

        JSONObject fields = FirestoreDocumentUtil.getFields(document);

        String ruleId = FirestoreDocumentUtil.getString(fields, "ruleId");
        String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
        String resourceId = FirestoreDocumentUtil.getString(fields, "resourceId");
        String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");
        String dayOfWeek = FirestoreDocumentUtil.getString(fields, "dayOfWeek");
        String startTime = FirestoreDocumentUtil.getString(fields, "startTime");
        String endTime = FirestoreDocumentUtil.getString(fields, "endTime");
        int slotIntervalMinutes = FirestoreDocumentUtil.getInt(fields, "slotIntervalMinutes", 60);
        int minimumBookingMinutes = FirestoreDocumentUtil.getInt(fields, "minimumBookingMinutes", 60);
        int maximumBookingMinutes = FirestoreDocumentUtil.getInt(fields, "maximumBookingMinutes", 240);
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);
        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        return new AvailabilityRule(
                ruleId, spaceId, resourceId, ownerId, dayOfWeek,
                startTime, endTime, slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes,
                active, createdAt, updatedAt);
    }

    private JSONObject blockToFirestoreDocument(AvailabilityBlock block) {
        JSONObject fields = new JSONObject();

        fields.put("blockId", FirestoreDocumentUtil.stringValue(block.getBlockId()));
        fields.put("spaceId", FirestoreDocumentUtil.stringValue(block.getSpaceId()));
        fields.put("resourceId", FirestoreDocumentUtil.stringValue(block.getResourceId()));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(block.getOwnerId()));
        fields.put("startAt", FirestoreDocumentUtil.integerValue(block.getStartAt()));
        fields.put("endAt", FirestoreDocumentUtil.integerValue(block.getEndAt()));
        fields.put("reason", FirestoreDocumentUtil.stringValue(block.getReason()));
        fields.put("status", FirestoreDocumentUtil.stringValue(block.getStatus()));
        fields.put("createdAt", FirestoreDocumentUtil.integerValue(block.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(block.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    private AvailabilityBlock blockFromFirestoreDocument(JSONObject document) {
        if (document == null) {
            return null;
        }

        JSONObject fields = FirestoreDocumentUtil.getFields(document);

        String blockId = FirestoreDocumentUtil.getString(fields, "blockId");
        String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
        String resourceId = FirestoreDocumentUtil.getString(fields, "resourceId");
        String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");
        long startAt = FirestoreDocumentUtil.getLong(fields, "startAt", 0L);
        long endAt = FirestoreDocumentUtil.getLong(fields, "endAt", 0L);
        String reason = FirestoreDocumentUtil.getString(fields, "reason");
        String status = FirestoreDocumentUtil.getString(fields, "status");
        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        return new AvailabilityBlock(
                blockId, spaceId, resourceId, ownerId,
                startAt, endAt, reason, status, createdAt, updatedAt);
    }
}
