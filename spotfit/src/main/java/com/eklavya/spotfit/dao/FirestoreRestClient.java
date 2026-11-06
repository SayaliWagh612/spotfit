// package com.eklavya.spotfit.dao;

// import com.eklavya.spotfit.config.FirebaseConfig;
// import com.eklavya.spotfit.constants.OperationCode;
// import com.eklavya.spotfit.model.OperationResult;
// import com.eklavya.spotfit.util.FirestoreDocumentUtil;
// import org.json.JSONArray;
// import org.json.JSONObject;

// import java.net.URI;
// import java.net.URLEncoder;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.net.http.HttpTimeoutException;
// import java.nio.charset.StandardCharsets;
// import java.time.Duration;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.CompletableFuture;

// /**
//  * Reusable asynchronous HTTP client for interacting with Cloud Firestore REST
//  * API.
//  */
// public class FirestoreRestClient {

//     private final HttpClient httpClient;

//     public FirestoreRestClient() {
//         this.httpClient = HttpClient.newBuilder()
//                 .connectTimeout(Duration.ofSeconds(10))
//                 .build();
//     }

//     public FirestoreRestClient(HttpClient httpClient) {
//         this.httpClient = (httpClient != null) ? httpClient
//                 : HttpClient.newBuilder()
//                         .connectTimeout(Duration.ofSeconds(10))
//                         .build();
//     }

//     public CompletableFuture<OperationResult<JSONObject>> getDocument(
//             String collection,
//             String documentId,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(pathResult.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();

//             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                     .thenApply(response -> {
//                         int statusCode = response.statusCode();

//                         if (statusCode >= 200 && statusCode < 300) {
//                             try {
//                                 String body = response.body();
//                                 JSONObject json = (body != null && !body.isBlank()) ? new JSONObject(body)
//                                         : new JSONObject();
//                                 return OperationResult.success(json);
//                             } catch (Exception e) {
//                                 return OperationResult.<JSONObject>failure(OperationCode.UNKNOWN_ERROR,
//                                         "Failed to parse Firestore JSON response.");
//                             }
//                         } else {
//                             return mapHttpErrorToJsonResult(statusCode, response.body());
//                         }
//                     })
//                     .exceptionally(this::handleTransportErrorJson);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create getDocument request."));
//         }
//     }

//     /**
//      * Performs a narrowly-scoped, anonymous direct document GET exclusively for
//      * Manager invitation pre-registration validation.
//      * Hard-restricted to CollectionName.MANAGER_INVITATIONS.
//      * Does NOT attach any Authorization header (no Bearer token).
//      */
//     CompletableFuture<OperationResult<JSONObject>> getPublicManagerInvitation(String invitationCode) {
//         if (!FirebaseConfig.isConfigured()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available."));
//         }
//         if (invitationCode == null || invitationCode.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invitation code cannot be blank."));
//         }

//         OperationResult<Void> codeCheck = validatePathSegment(invitationCode);
//         if (codeCheck.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(codeCheck.getErrorCode(), codeCheck.getMessage()));
//         }

//         String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
//         if (baseUrl == null || baseUrl.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured."));
//         }

//         String collection = com.eklavya.spotfit.constants.CollectionName.MANAGER_INVITATIONS;
//         String encodedCollection = URLEncoder.encode(collection, StandardCharsets.UTF_8);
//         String encodedDocumentId = URLEncoder.encode(invitationCode.trim().toUpperCase(), StandardCharsets.UTF_8);
//         String url = baseUrl + "/" + encodedCollection + "/" + encodedDocumentId;

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(url))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                             "Failed to create public invitation GET request."));
//         }
//     }

//     public CompletableFuture<OperationResult<JSONObject>> setDocument(
//             String collection,
//             String documentId,
//             JSONObject document,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }
//         if (document == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(pathResult.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Content-Type", "application/json")
//                     .header("Accept", "application/json")
//                     .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create setDocument request."));
//         }
//     }

//     public CompletableFuture<OperationResult<JSONObject>> updateDocument(
//             String collection,
//             String documentId,
//             JSONObject document,
//             List<String> updateFields,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }
//         if (document == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
//         }

//         try {
//             StringBuilder urlBuilder = new StringBuilder(pathResult.getData());
//             if (updateFields != null && !updateFields.isEmpty()) {
//                 boolean first = true;
//                 for (String field : updateFields) {
//                     if (field != null && !field.isBlank()) {
//                         urlBuilder.append(first ? "?" : "&")
//                                 .append("updateMask.fieldPaths=")
//                                 .append(URLEncoder.encode(field.trim(), StandardCharsets.UTF_8));
//                         first = false;
//                     }
//                 }
//             }

//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlBuilder.toString()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Content-Type", "application/json")
//                     .header("Accept", "application/json")
//                     .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create updateDocument request."));
//         }
//     }

//     public CompletableFuture<OperationResult<Void>> deleteDocument(
//             String collection,
//             String documentId,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(pathResult.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Accept", "application/json")
//                     .DELETE()
//                     .build();

//             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                     .thenApply(response -> {
//                         int statusCode = response.statusCode();
//                         if (statusCode >= 200 && statusCode < 300) {
//                             return OperationResult.<Void>success(null, "Document deleted successfully.");
//                         } else {
//                             return mapHttpErrorToVoidResult(statusCode, response.body());
//                         }
//                     })
//                     .exceptionally(this::handleTransportErrorVoid);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Failed to create deleteDocument request."));
//         }
//     }

//     private OperationResult<String> validateAndBuildUrl(String collection, String documentId, String idToken) {
//         if (!FirebaseConfig.isConfigured()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                     "ID token is required for Firestore operation.");
//         }
//         if (collection == null || collection.isBlank() || documentId == null || documentId.isBlank()) {
//             return OperationResult.failure(OperationCode.INVALID_ARGUMENT,
//                     "Collection and documentId cannot be blank.");
//         }

//         String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
//         if (baseUrl == null || baseUrl.isBlank()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
//         }

//         String encodedCollection = URLEncoder.encode(collection.trim(), StandardCharsets.UTF_8);
//         String encodedDocumentId = URLEncoder.encode(documentId.trim(), StandardCharsets.UTF_8);
//         String url = baseUrl + "/" + encodedCollection + "/" + encodedDocumentId;

//         return OperationResult.success(url);
//     }

//     public OperationResult<String> validateAndBuildNestedUrl(
//             String parentCollection,
//             String parentDocumentId,
//             String childCollection,
//             String childDocumentId,
//             String idToken) {

//         if (!FirebaseConfig.isConfigured()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                     "ID token is required for Firestore operation.");
//         }

//         OperationResult<Void> parentColCheck = validatePathSegment(parentCollection);
//         if (parentColCheck.isFailure())
//             return OperationResult.failure(parentColCheck.getErrorCode(), parentColCheck.getMessage());

//         OperationResult<Void> parentDocCheck = validatePathSegment(parentDocumentId);
//         if (parentDocCheck.isFailure())
//             return OperationResult.failure(parentDocCheck.getErrorCode(), parentDocCheck.getMessage());

//         OperationResult<Void> childColCheck = validatePathSegment(childCollection);
//         if (childColCheck.isFailure())
//             return OperationResult.failure(childColCheck.getErrorCode(), childColCheck.getMessage());

//         String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
//         if (baseUrl == null || baseUrl.isBlank()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
//         }

//         StringBuilder pathBuilder = new StringBuilder(baseUrl)
//                 .append("/").append(URLEncoder.encode(parentCollection.trim(), StandardCharsets.UTF_8))
//                 .append("/").append(URLEncoder.encode(parentDocumentId.trim(), StandardCharsets.UTF_8))
//                 .append("/").append(URLEncoder.encode(childCollection.trim(), StandardCharsets.UTF_8));

//         if (childDocumentId != null && !childDocumentId.isBlank()) {
//             OperationResult<Void> childDocCheck = validatePathSegment(childDocumentId);
//             if (childDocCheck.isFailure())
//                 return OperationResult.failure(childDocCheck.getErrorCode(), childDocCheck.getMessage());
//             pathBuilder.append("/").append(URLEncoder.encode(childDocumentId.trim(), StandardCharsets.UTF_8));
//         }

//         return OperationResult.success(pathBuilder.toString());
//     }

//     public OperationResult<String> validateAndBuildSegmentUrl(List<String> pathSegments, String idToken) {
//         if (!FirebaseConfig.isConfigured()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                     "ID token is required for Firestore operation.");
//         }
//         if (pathSegments == null || pathSegments.isEmpty()) {
//             return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segments cannot be empty.");
//         }

//         String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
//         if (baseUrl == null || baseUrl.isBlank()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
//         }

//         StringBuilder pathBuilder = new StringBuilder(baseUrl);
//         for (String segment : pathSegments) {
//             OperationResult<Void> segCheck = validatePathSegment(segment);
//             if (segCheck.isFailure()) {
//                 return OperationResult.failure(segCheck.getErrorCode(), segCheck.getMessage());
//             }
//             pathBuilder.append("/").append(URLEncoder.encode(segment.trim(), StandardCharsets.UTF_8));
//         }

//         return OperationResult.success(pathBuilder.toString());
//     }

//     public OperationResult<String> validateAndBuildPublicSegmentUrl(List<String> pathSegments) {
//         if (!FirebaseConfig.isConfigured()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
//         }
//         if (pathSegments == null || pathSegments.isEmpty()) {
//             return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segments cannot be empty.");
//         }
//         String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
//         if (baseUrl == null || baseUrl.isBlank()) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
//         }
//         StringBuilder pathBuilder = new StringBuilder(baseUrl);
//         for (String segment : pathSegments) {
//             OperationResult<Void> segCheck = validatePathSegment(segment);
//             if (segCheck.isFailure()) {
//                 return OperationResult.failure(segCheck.getErrorCode(), segCheck.getMessage());
//             }
//             pathBuilder.append("/").append(URLEncoder.encode(segment.trim(), StandardCharsets.UTF_8));
//         }
//         return OperationResult.success(pathBuilder.toString());
//     }

//     public CompletableFuture<OperationResult<JSONObject>> getDocumentBySegments(List<String> pathSegments,
//             String idToken) {
//         OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
//         if (urlRes.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlRes.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                             "Failed to create getDocumentBySegments request."));
//         }
//     }

//     public CompletableFuture<OperationResult<JSONObject>> getPublicDocumentBySegments(List<String> pathSegments) {
//         OperationResult<String> urlRes = validateAndBuildPublicSegmentUrl(pathSegments);
//         if (urlRes.isFailure()) {
//             return CompletableFuture
//                     .completedFuture(OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
//         }
//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlRes.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();
//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                     "Failed to create getPublicDocumentBySegments request."));
//         }
//     }

//     public CompletableFuture<OperationResult<JSONObject>> setDocumentBySegments(List<String> pathSegments,
//             JSONObject document, String idToken) {
//         OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
//         if (urlRes.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
//         }
//         if (document == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlRes.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Content-Type", "application/json")
//                     .header("Accept", "application/json")
//                     .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                             "Failed to create setDocumentBySegments request."));
//         }
//     }

//     public CompletableFuture<OperationResult<List<JSONObject>>> listDocumentsBySegments(List<String> pathSegments,
//             String idToken) {
//         OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
//         if (urlRes.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlRes.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();

//             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                     .thenApply(response -> {
//                         int statusCode = response.statusCode();
//                         if (statusCode >= 200 && statusCode < 300) {
//                             try {
//                                 String body = response.body();
//                                 List<JSONObject> documents = new ArrayList<>();
//                                 if (body != null && !body.isBlank()) {
//                                     JSONObject json = new JSONObject(body);
//                                     if (json.has("documents")) {
//                                         JSONArray array = json.getJSONArray("documents");
//                                         for (int i = 0; i < array.length(); i++) {
//                                             documents.add(array.getJSONObject(i));
//                                         }
//                                     }
//                                 }
//                                 return OperationResult.<List<JSONObject>>success(documents);
//                             } catch (Exception e) {
//                                 return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
//                                         "Failed to parse Firestore list response.");
//                             }
//                         } else if (statusCode == 404) {
//                             return OperationResult.<List<JSONObject>>success(new ArrayList<>());
//                         } else {
//                             return mapHttpErrorToListResult(statusCode);
//                         }
//                     })
//                     .exceptionally(this::handleTransportErrorList);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                             "Failed to create listDocumentsBySegments request."));
//         }
//     }

//     public CompletableFuture<OperationResult<List<JSONObject>>> listPublicDocumentsBySegments(
//             List<String> pathSegments) {
//         OperationResult<String> urlRes = validateAndBuildPublicSegmentUrl(pathSegments);
//         if (urlRes.isFailure()) {
//             return CompletableFuture
//                     .completedFuture(OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
//         }
//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlRes.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();
//             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                     .thenApply(response -> {
//                         int statusCode = response.statusCode();
//                         if (statusCode >= 200 && statusCode < 300) {
//                             try {
//                                 String body = response.body();
//                                 List<JSONObject> documents = new ArrayList<>();
//                                 if (body != null && !body.isBlank()) {
//                                     JSONObject json = new JSONObject(body);
//                                     if (json.has("documents")) {
//                                         JSONArray array = json.getJSONArray("documents");
//                                         for (int i = 0; i < array.length(); i++) {
//                                             documents.add(array.getJSONObject(i));
//                                         }
//                                     }
//                                 }
//                                 return OperationResult.<List<JSONObject>>success(documents);
//                             } catch (Exception e) {
//                                 return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
//                                         "Failed to parse public list response.");
//                             }
//                         } else if (statusCode == 404) {
//                             return OperationResult.<List<JSONObject>>success(new ArrayList<>());
//                         } else {
//                             return mapHttpErrorToListResult(statusCode);
//                         }
//                     }).exceptionally(this::handleTransportErrorList);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                     "Failed to create listPublicDocuments request."));
//         }
//     }

//     public CompletableFuture<OperationResult<Void>> deleteDocumentBySegments(List<String> pathSegments,
//             String idToken) {
//         OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
//         if (urlRes.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlRes.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Accept", "application/json")
//                     .DELETE()
//                     .build();

//             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                     .thenApply(response -> {
//                         int statusCode = response.statusCode();
//                         if (statusCode >= 200 && statusCode < 300) {
//                             return OperationResult.<Void>success(null, "Document deleted successfully.");
//                         } else if (statusCode == 404) {
//                             // Removing an absent favorite should produce a stable success/not-found result
//                             return OperationResult.<Void>success(null, "Document already deleted or not found.");
//                         } else {
//                             return mapHttpErrorToVoidResult(statusCode, response.body());
//                         }
//                     })
//                     .exceptionally(this::handleTransportErrorVoid);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                             "Failed to create deleteDocumentBySegments request."));
//         }
//     }

//     private OperationResult<Void> validatePathSegment(String segment) {
//         if (segment == null || segment.isBlank()) {
//             return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segment cannot be null or blank.");
//         }
//         String trimmed = segment.trim();
//         if (trimmed.contains("/") || trimmed.contains("\\") || trimmed.contains("..")
//                 || trimmed.contains("?") || trimmed.contains("#") || trimmed.contains("%")) {
//             return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segment contains invalid characters.");
//         }
//         return OperationResult.success(null);
//     }

//     public CompletableFuture<OperationResult<JSONObject>> getNestedDocument(
//             String parentCollection,
//             String parentDocumentId,
//             String childCollection,
//             String childDocumentId,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildNestedUrl(
//                 parentCollection, parentDocumentId, childCollection, childDocumentId, idToken);

//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(pathResult.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create getNestedDocument request."));
//         }
//     }

//     public CompletableFuture<OperationResult<JSONObject>> setNestedDocument(
//             String parentCollection,
//             String parentDocumentId,
//             String childCollection,
//             String childDocumentId,
//             JSONObject document,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildNestedUrl(
//                 parentCollection, parentDocumentId, childCollection, childDocumentId, idToken);

//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }
//         if (document == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(pathResult.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Content-Type", "application/json")
//                     .header("Accept", "application/json")
//                     .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create setNestedDocument request."));
//         }
//     }

//     public CompletableFuture<OperationResult<JSONObject>> updateNestedDocument(
//             String parentCollection,
//             String parentDocumentId,
//             String childCollection,
//             String childDocumentId,
//             JSONObject document,
//             List<String> updateFields,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildNestedUrl(
//                 parentCollection, parentDocumentId, childCollection, childDocumentId, idToken);

//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }
//         if (document == null) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
//         }

//         try {
//             StringBuilder urlBuilder = new StringBuilder(pathResult.getData());
//             if (updateFields != null && !updateFields.isEmpty()) {
//                 boolean first = true;
//                 for (String field : updateFields) {
//                     if (field != null && !field.isBlank()) {
//                         urlBuilder.append(first ? "?" : "&")
//                                 .append("updateMask.fieldPaths=")
//                                 .append(URLEncoder.encode(field.trim(), StandardCharsets.UTF_8));
//                         first = false;
//                     }
//                 }
//             }

//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(urlBuilder.toString()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Content-Type", "application/json")
//                     .header("Accept", "application/json")
//                     .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
//                     .build();

//             return executeJsonObjectRequest(request);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                             "Failed to create updateNestedDocument request."));
//         }
//     }

//     public CompletableFuture<OperationResult<List<JSONObject>>> listNestedDocuments(
//             String parentCollection,
//             String parentDocumentId,
//             String childCollection,
//             String idToken) {

//         OperationResult<String> pathResult = validateAndBuildNestedUrl(
//                 parentCollection, parentDocumentId, childCollection, null, idToken);

//         if (pathResult.isFailure()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
//         }

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(pathResult.getData()))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Accept", "application/json")
//                     .GET()
//                     .build();

//             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                     .thenApply(response -> {
//                         int statusCode = response.statusCode();
//                         if (statusCode >= 200 && statusCode < 300) {
//                             try {
//                                 String body = response.body();
//                                 List<JSONObject> documents = new ArrayList<>();
//                                 if (body != null && !body.isBlank()) {
//                                     JSONObject json = new JSONObject(body);
//                                     if (json.has("documents")) {
//                                         JSONArray array = json.getJSONArray("documents");
//                                         for (int i = 0; i < array.length(); i++) {
//                                             documents.add(array.getJSONObject(i));
//                                         }
//                                     }
//                                 }
//                                 return OperationResult.<List<JSONObject>>success(documents);
//                             } catch (Exception e) {
//                                 return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
//                                         "Failed to parse Firestore list response.");
//                             }
//                         } else if (statusCode == 404) {
//                             return OperationResult.<List<JSONObject>>success(new ArrayList<>());
//                         } else {
//                             return mapHttpErrorToListResult(statusCode);
//                         }
//                     })
//                     .exceptionally(this::handleTransportErrorList);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED",
//                             "Failed to create listNestedDocuments request."));
//         }
//     }

//     private CompletableFuture<OperationResult<JSONObject>> executeJsonObjectRequest(HttpRequest request) {
//         return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                 .thenApply(response -> {
//                     int statusCode = response.statusCode();
//                     if (statusCode >= 200 && statusCode < 300) {
//                         try {
//                             String body = response.body();
//                             JSONObject json = (body != null && !body.isBlank()) ? new JSONObject(body)
//                                     : new JSONObject();
//                             return OperationResult.success(json);
//                         } catch (Exception e) {
//                             return OperationResult.<JSONObject>failure(OperationCode.UNKNOWN_ERROR,
//                                     "Failed to parse Firestore JSON response.");
//                         }
//                     } else {
//                         return mapHttpErrorToJsonResult(statusCode, response.body());
//                     }
//                 })
//                 .exceptionally(this::handleTransportErrorJson);
//     }

//     private OperationResult<JSONObject> mapHttpErrorToJsonResult(int statusCode) {
//         return mapHttpErrorToJsonResult(statusCode, null);
//     }

//     private OperationResult<Void> mapHttpErrorToVoidResult(int statusCode) {
//         return mapHttpErrorToVoidResult(statusCode, null);
//     }

//     private OperationResult<JSONObject> mapHttpErrorToJsonResult(int statusCode, String responseBody) {
//         String detail = extractSanitizedFirebaseError(statusCode, responseBody);
//         switch (statusCode) {
//             case 400:
//                 return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid argument: " + detail);
//             case 401:
//                 return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                         "Authentication required: " + detail);
//             case 403:
//                 return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied: " + detail);
//             case 404:
//                 return OperationResult.failure(OperationCode.NOT_FOUND, "Not found: " + detail);
//             case 429:
//                 return OperationResult.failure(OperationCode.NETWORK_ERROR, "Too many requests: " + detail);
//             default:
//                 return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore error: " + detail);
//         }
//     }

//     private OperationResult<Void> mapHttpErrorToVoidResult(int statusCode, String responseBody) {
//         String detail = extractSanitizedFirebaseError(statusCode, responseBody);
//         switch (statusCode) {
//             case 400:
//                 return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid argument: " + detail);
//             case 401:
//                 return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                         "Authentication required: " + detail);
//             case 403:
//                 return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied: " + detail);
//             case 404:
//                 return OperationResult.failure(OperationCode.NOT_FOUND, "Not found: " + detail);
//             case 429:
//                 return OperationResult.failure(OperationCode.NETWORK_ERROR, "Too many requests: " + detail);
//             default:
//                 return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore error: " + detail);
//         }
//     }

//     private String extractSanitizedFirebaseError(int statusCode, String responseBody) {
//         if (responseBody != null && !responseBody.isBlank()) {
//             try {
//                 JSONObject json = new JSONObject(responseBody);
//                 if (json.has("error")) {
//                     JSONObject err = json.getJSONObject("error");
//                     String status = err.optString("status", "");
//                     String message = err.optString("message", "");
//                     if (!message.isBlank()) {
//                         message = message.replaceAll("Bearer\\s+[A-Za-z0-9._-]+", "[REDACTED_TOKEN]");
//                         return (!status.isBlank() ? status + " — " : "") + message + " (HTTP " + statusCode + ")";
//                     }
//                 }
//             } catch (Exception ignored) {
//             }
//         }
//         return "HTTP " + statusCode;
//     }

//     private OperationResult<JSONObject> handleTransportErrorJson(Throwable throwable) {
//         Throwable cause = (throwable.getCause() != null) ? throwable.getCause() : throwable;
//         if (cause instanceof HttpTimeoutException) {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR, "The request to Firestore timed out.");
//         } else if (cause instanceof java.net.ConnectException) {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR, "Could not connect to Firestore service.");
//         } else if (cause instanceof InterruptedException) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "The operation was interrupted.");
//         } else {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR,
//                     "Network error occurred while communicating with Firestore.");
//         }
//     }

//     private OperationResult<Void> handleTransportErrorVoid(Throwable throwable) {
//         Throwable cause = (throwable.getCause() != null) ? throwable.getCause() : throwable;
//         if (cause instanceof HttpTimeoutException) {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR, "The request to Firestore timed out.");
//         } else if (cause instanceof java.net.ConnectException) {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR, "Could not connect to Firestore service.");
//         } else if (cause instanceof InterruptedException) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "The operation was interrupted.");
//         } else {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR,
//                     "Network error occurred while communicating with Firestore.");
//         }
//     }

//     public CompletableFuture<OperationResult<List<JSONObject>>> queryDocuments(
//             String collectionName,
//             String fieldName,
//             Object equalValue,
//             String idToken) {

//         if (fieldName == null || fieldName.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Field name cannot be blank."));
//         }
//         Map<String, Object> filters = new java.util.LinkedHashMap<>();
//         filters.put(fieldName, equalValue);
//         return queryDocuments(collectionName, filters, idToken);
//     }

//     public CompletableFuture<OperationResult<List<JSONObject>>> queryDocuments(
//             String collectionName,
//             Map<String, Object> equalityFilters,
//             String idToken) {

//         if (!FirebaseConfig.isConfigured()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available."));
//         }
//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required for query."));
//         }
//         if (collectionName == null || collectionName.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Collection name cannot be blank."));
//         }
//         if (equalityFilters == null || equalityFilters.isEmpty()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Equality filters map cannot be empty."));
//         }

//         List<JSONObject> fieldFilters = new ArrayList<>();
//         for (Map.Entry<String, Object> entry : equalityFilters.entrySet()) {
//             String fName = entry.getKey();
//             if (fName == null || fName.isBlank()) {
//                 return CompletableFuture.completedFuture(
//                         OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Filter key cannot be blank."));
//             }
//             Object val = entry.getValue();
//             JSONObject valueObj;
//             if (val instanceof Boolean) {
//                 valueObj = FirestoreDocumentUtil.booleanValue((Boolean) val);
//             } else if (val instanceof Number) {
//                 valueObj = FirestoreDocumentUtil.integerValue(((Number) val).longValue());
//             } else {
//                 valueObj = FirestoreDocumentUtil.stringValue(val != null ? val.toString() : "");
//             }
//             JSONObject fieldFilter = new JSONObject()
//                     .put("field", new JSONObject().put("fieldPath", fName.trim()))
//                     .put("op", "EQUAL")
//                     .put("value", valueObj);
//             fieldFilters.add(new JSONObject().put("fieldFilter", fieldFilter));
//         }

//         JSONObject whereObj;
//         if (fieldFilters.isEmpty()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Equality filters map cannot be empty."));
//         } else if (fieldFilters.size() == 1) {
//             whereObj = fieldFilters.get(0);
//         } else {
//             JSONArray filtersArray = new JSONArray();
//             for (JSONObject ff : fieldFilters) {
//                 filtersArray.put(ff);
//             }
//             JSONObject compositeFilter = new JSONObject()
//                     .put("op", "AND")
//                     .put("filters", filtersArray);
//             whereObj = new JSONObject().put("compositeFilter", compositeFilter);
//         }

//         JSONArray fromArray = new JSONArray()
//                 .put(new JSONObject().put("collectionId", collectionName.trim()));

//         JSONObject structuredQuery = new JSONObject()
//                 .put("from", fromArray)
//                 .put("where", whereObj);

//         JSONObject payload = new JSONObject().put("structuredQuery", structuredQuery);

//         String runQueryUrl = FirebaseConfig.getFirestoreDocumentsUrl() + ":runQuery";

//         HttpRequest request = HttpRequest.newBuilder()
//                 .uri(URI.create(runQueryUrl))
//                 .timeout(Duration.ofSeconds(30))
//                 .header("Authorization", "Bearer " + idToken.trim())
//                 .header("Content-Type", "application/json")
//                 .header("Accept", "application/json")
//                 .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
//                 .build();

//         return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                 .thenApply(response -> {
//                     int statusCode = response.statusCode();
//                     if (statusCode >= 200 && statusCode < 300) {
//                         try {
//                             String body = response.body();
//                             List<JSONObject> documents = new ArrayList<>();
//                             if (body != null && !body.isBlank()) {
//                                 JSONArray array = new JSONArray(body);
//                                 for (int i = 0; i < array.length(); i++) {
//                                     JSONObject item = array.optJSONObject(i);
//                                     if (item != null && item.has("document")) {
//                                         documents.add(item.getJSONObject("document"));
//                                     }
//                                 }
//                             }
//                             return OperationResult.success(documents);
//                         } catch (Exception e) {
//                             return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
//                                     "Failed to parse Firestore query response.");
//                         }
//                     } else {
//                         return mapHttpErrorToListResult(statusCode);
//                     }
//                 })
//                 .exceptionally(this::handleTransportErrorList);
//     }

//     public CompletableFuture<OperationResult<List<JSONObject>>> publicQueryDocuments(
//             String collectionName,
//             Map<String, Object> equalityFilters) {

//         if (!FirebaseConfig.isConfigured()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available."));
//         }
//         if (collectionName == null || collectionName.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Collection name cannot be blank."));
//         }
//         if (equalityFilters == null || equalityFilters.isEmpty()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Equality filters map cannot be empty."));
//         }

//         List<JSONObject> fieldFilters = new ArrayList<>();
//         for (Map.Entry<String, Object> entry : equalityFilters.entrySet()) {
//             String fName = entry.getKey();
//             if (fName == null || fName.isBlank()) {
//                 return CompletableFuture.completedFuture(
//                         OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Filter key cannot be blank."));
//             }
//             Object val = entry.getValue();
//             JSONObject valueObj;
//             if (val instanceof Boolean) {
//                 valueObj = FirestoreDocumentUtil.booleanValue((Boolean) val);
//             } else if (val instanceof Number) {
//                 valueObj = FirestoreDocumentUtil.integerValue(((Number) val).longValue());
//             } else {
//                 valueObj = FirestoreDocumentUtil.stringValue(val != null ? val.toString() : "");
//             }
//             JSONObject fieldFilter = new JSONObject()
//                     .put("field", new JSONObject().put("fieldPath", fName.trim()))
//                     .put("op", "EQUAL")
//                     .put("value", valueObj);
//             fieldFilters.add(new JSONObject().put("fieldFilter", fieldFilter));
//         }

//         JSONObject whereObj;
//         if (fieldFilters.size() == 1) {
//             whereObj = fieldFilters.get(0);
//         } else {
//             JSONArray filtersArray = new JSONArray();
//             for (JSONObject ff : fieldFilters) {
//                 filtersArray.put(ff);
//             }
//             JSONObject compositeFilter = new JSONObject()
//                     .put("op", "AND")
//                     .put("filters", filtersArray);
//             whereObj = new JSONObject().put("compositeFilter", compositeFilter);
//         }

//         JSONArray fromArray = new JSONArray()
//                 .put(new JSONObject().put("collectionId", collectionName.trim()));

//         JSONObject structuredQuery = new JSONObject()
//                 .put("from", fromArray)
//                 .put("where", whereObj);

//         JSONObject payload = new JSONObject().put("structuredQuery", structuredQuery);

//         String runQueryUrl = FirebaseConfig.getFirestoreDocumentsUrl() + ":runQuery";

//         HttpRequest request = HttpRequest.newBuilder()
//                 .uri(URI.create(runQueryUrl))
//                 .timeout(Duration.ofSeconds(30))
//                 .header("Content-Type", "application/json")
//                 .header("Accept", "application/json")
//                 .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
//                 .build();

//         return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                 .thenApply(response -> {
//                     int statusCode = response.statusCode();
//                     if (statusCode >= 200 && statusCode < 300) {
//                         try {
//                             String body = response.body();
//                             List<JSONObject> documents = new ArrayList<>();
//                             if (body != null && !body.isBlank()) {
//                                 JSONArray array = new JSONArray(body);
//                                 for (int i = 0; i < array.length(); i++) {
//                                     JSONObject item = array.optJSONObject(i);
//                                     if (item != null && item.has("document")) {
//                                         documents.add(item.getJSONObject("document"));
//                                     }
//                                 }
//                             }
//                             return OperationResult.success(documents);
//                         } catch (Exception e) {
//                             return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
//                                     "Failed to parse public query response.");
//                         }
//                     } else {
//                         return mapHttpErrorToListResult(statusCode);
//                     }
//                 })
//                 .exceptionally(this::handleTransportErrorList);
//     }

//     private OperationResult<List<JSONObject>> mapHttpErrorToListResult(int statusCode) {
//         switch (statusCode) {
//             case 400:
//                 return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid argument (HTTP 400).");
//             case 401:
//                 return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                         "Authentication required. Token may be expired (HTTP 401).");
//             case 403:
//                 return OperationResult.failure(OperationCode.PERMISSION_DENIED,
//                         "Access denied. Insufficient permissions (HTTP 403).");
//             case 404:
//                 return OperationResult.failure(OperationCode.NOT_FOUND,
//                         "The requested resource was not found (HTTP 404).");
//             case 429:
//                 return OperationResult.failure(OperationCode.NETWORK_ERROR,
//                         "Too many requests. Please try again later (HTTP 429).");
//             case 500:
//             case 502:
//             case 503:
//             case 504:
//                 return OperationResult.failure(OperationCode.NETWORK_ERROR,
//                         "Firestore service is temporarily unavailable (HTTP " + statusCode + ").");
//             default:
//                 return OperationResult.failure(OperationCode.UNKNOWN_ERROR,
//                         "Firestore operation failed (HTTP " + statusCode + ").");
//         }
//     }

//     public CompletableFuture<OperationResult<JSONObject>> commitBatchWrites(
//             JSONArray writes,
//             String idToken) {

//         if (idToken == null || idToken.isBlank()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
//                             "Authentication token is required for batch commit."));
//         }
//         if (writes == null || writes.isEmpty()) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Batch writes payload cannot be empty."));
//         }

//         String commitUrl = FirebaseConfig.getFirestoreDocumentsUrl() + ":commit";

//         JSONObject payload = new JSONObject().put("writes", writes);

//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(commitUrl))
//                     .timeout(Duration.ofSeconds(30))
//                     .header("Authorization", "Bearer " + idToken.trim())
//                     .header("Content-Type", "application/json")
//                     .header("Accept", "application/json")
//                     .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
//                     .build();

//             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                     .thenApply(response -> {
//                         int statusCode = response.statusCode();
//                         if (statusCode >= 200 && statusCode < 300) {
//                             try {
//                                 String body = response.body();
//                                 JSONObject json = (body != null && !body.isBlank()) ? new JSONObject(body)
//                                         : new JSONObject();
//                                 return OperationResult.success(json);
//                             } catch (Exception e) {
//                                 return OperationResult.<JSONObject>failure(OperationCode.UNKNOWN_ERROR,
//                                         "Failed to parse Firestore batch commit response.");
//                             }
//                         } else {
//                             return mapHttpErrorToJsonResult(statusCode, response.body());
//                         }
//                     })
//                     .exceptionally(this::handleTransportErrorJson);
//         } catch (Exception e) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create batch commit request."));
//         }
//     }

//     private OperationResult<List<JSONObject>> handleTransportErrorList(Throwable throwable) {
//         Throwable cause = (throwable.getCause() != null) ? throwable.getCause() : throwable;
//         if (cause instanceof HttpTimeoutException) {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR, "The request to Firestore timed out.");
//         } else if (cause instanceof java.net.ConnectException) {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR, "Could not connect to Firestore service.");
//         } else if (cause instanceof InterruptedException) {
//             return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "The operation was interrupted.");
//         } else {
//             return OperationResult.failure(OperationCode.NETWORK_ERROR,
//                     "Network error occurred while communicating with Firestore.");
//         }
//     }
// }
package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.config.FirebaseConfig;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Reusable asynchronous HTTP client for interacting with Cloud Firestore REST
 * API.
 */
public class FirestoreRestClient {

    private final HttpClient httpClient;

    public FirestoreRestClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public FirestoreRestClient(HttpClient httpClient) {
        this.httpClient = (httpClient != null) ? httpClient
                : HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
    }

    public CompletableFuture<OperationResult<JSONObject>> getDocument(
            String collection,
            String documentId,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pathResult.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();

                        if (statusCode >= 200 && statusCode < 300) {
                            try {
                                String body = response.body();
                                JSONObject json = (body != null && !body.isBlank()) ? new JSONObject(body)
                                        : new JSONObject();
                                return OperationResult.success(json);
                            } catch (Exception e) {
                                return OperationResult.<JSONObject>failure(OperationCode.UNKNOWN_ERROR,
                                        "Failed to parse Firestore JSON response.");
                            }
                        } else {
                            return mapHttpErrorToJsonResult(statusCode, response.body());
                        }
                    })
                    .exceptionally(this::handleTransportErrorJson);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create getDocument request."));
        }
    }

    /**
     * Performs a narrowly-scoped, anonymous direct document GET exclusively for
     * Manager invitation pre-registration validation.
     * Hard-restricted to CollectionName.MANAGER_INVITATIONS.
     * Does NOT attach any Authorization header (no Bearer token).
     */
    CompletableFuture<OperationResult<JSONObject>> getPublicManagerInvitation(String invitationCode) {
        if (!FirebaseConfig.isConfigured()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available."));
        }
        if (invitationCode == null || invitationCode.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invitation code cannot be blank."));
        }

        OperationResult<Void> codeCheck = validatePathSegment(invitationCode);
        if (codeCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(codeCheck.getErrorCode(), codeCheck.getMessage()));
        }

        String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured."));
        }

        String collection = com.eklavya.spotfit.constants.CollectionName.MANAGER_INVITATIONS;
        String encodedCollection = URLEncoder.encode(collection, StandardCharsets.UTF_8);
        String encodedDocumentId = URLEncoder.encode(invitationCode.trim().toUpperCase(), StandardCharsets.UTF_8);
        String url = baseUrl + "/" + encodedCollection + "/" + encodedDocumentId;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                            "Failed to create public invitation GET request."));
        }
    }

    public CompletableFuture<OperationResult<JSONObject>> setDocument(
            String collection,
            String documentId,
            JSONObject document,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }
        if (document == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pathResult.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create setDocument request."));
        }
    }

    public CompletableFuture<OperationResult<JSONObject>> updateDocument(
            String collection,
            String documentId,
            JSONObject document,
            List<String> updateFields,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }
        if (document == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
        }

        try {
            StringBuilder urlBuilder = new StringBuilder(pathResult.getData());
            if (updateFields != null && !updateFields.isEmpty()) {
                boolean first = true;
                for (String field : updateFields) {
                    if (field != null && !field.isBlank()) {
                        urlBuilder.append(first ? "?" : "&")
                                .append("updateMask.fieldPaths=")
                                .append(URLEncoder.encode(field.trim(), StandardCharsets.UTF_8));
                        first = false;
                    }
                }
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create updateDocument request."));
        }
    }

    public CompletableFuture<OperationResult<Void>> deleteDocument(
            String collection,
            String documentId,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildUrl(collection, documentId, idToken);
        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pathResult.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Accept", "application/json")
                    .DELETE()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            return OperationResult.<Void>success(null, "Document deleted successfully.");
                        } else {
                            return mapHttpErrorToVoidResult(statusCode, response.body());
                        }
                    })
                    .exceptionally(this::handleTransportErrorVoid);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Failed to create deleteDocument request."));
        }
    }

    private OperationResult<String> validateAndBuildUrl(String collection, String documentId, String idToken) {
        if (!FirebaseConfig.isConfigured()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
        }
        if (idToken == null || idToken.isBlank()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                    "ID token is required for Firestore operation.");
        }
        if (collection == null || collection.isBlank() || documentId == null || documentId.isBlank()) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                    "Collection and documentId cannot be blank.");
        }

        String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
        }

        String encodedCollection = URLEncoder.encode(collection.trim(), StandardCharsets.UTF_8);
        String encodedDocumentId = URLEncoder.encode(documentId.trim(), StandardCharsets.UTF_8);
        String url = baseUrl + "/" + encodedCollection + "/" + encodedDocumentId;

        return OperationResult.success(url);
    }

    public OperationResult<String> validateAndBuildNestedUrl(
            String parentCollection,
            String parentDocumentId,
            String childCollection,
            String childDocumentId,
            String idToken) {

        if (!FirebaseConfig.isConfigured()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
        }
        if (idToken == null || idToken.isBlank()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                    "ID token is required for Firestore operation.");
        }

        OperationResult<Void> parentColCheck = validatePathSegment(parentCollection);
        if (parentColCheck.isFailure())
            return OperationResult.failure(parentColCheck.getErrorCode(), parentColCheck.getMessage());

        OperationResult<Void> parentDocCheck = validatePathSegment(parentDocumentId);
        if (parentDocCheck.isFailure())
            return OperationResult.failure(parentDocCheck.getErrorCode(), parentDocCheck.getMessage());

        OperationResult<Void> childColCheck = validatePathSegment(childCollection);
        if (childColCheck.isFailure())
            return OperationResult.failure(childColCheck.getErrorCode(), childColCheck.getMessage());

        String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
        }

        StringBuilder pathBuilder = new StringBuilder(baseUrl)
                .append("/").append(URLEncoder.encode(parentCollection.trim(), StandardCharsets.UTF_8))
                .append("/").append(URLEncoder.encode(parentDocumentId.trim(), StandardCharsets.UTF_8))
                .append("/").append(URLEncoder.encode(childCollection.trim(), StandardCharsets.UTF_8));

        if (childDocumentId != null && !childDocumentId.isBlank()) {
            OperationResult<Void> childDocCheck = validatePathSegment(childDocumentId);
            if (childDocCheck.isFailure())
                return OperationResult.failure(childDocCheck.getErrorCode(), childDocCheck.getMessage());
            pathBuilder.append("/").append(URLEncoder.encode(childDocumentId.trim(), StandardCharsets.UTF_8));
        }

        return OperationResult.success(pathBuilder.toString());
    }

    public OperationResult<String> validateAndBuildSegmentUrl(List<String> pathSegments, String idToken) {
        if (!FirebaseConfig.isConfigured()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
        }
        if (idToken == null || idToken.isBlank()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                    "ID token is required for Firestore operation.");
        }
        if (pathSegments == null || pathSegments.isEmpty()) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segments cannot be empty.");
        }

        String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
        }

        StringBuilder pathBuilder = new StringBuilder(baseUrl);
        for (String segment : pathSegments) {
            OperationResult<Void> segCheck = validatePathSegment(segment);
            if (segCheck.isFailure()) {
                return OperationResult.failure(segCheck.getErrorCode(), segCheck.getMessage());
            }
            pathBuilder.append("/").append(URLEncoder.encode(segment.trim(), StandardCharsets.UTF_8));
        }

        return OperationResult.success(pathBuilder.toString());
    }

    public OperationResult<String> validateAndBuildPublicSegmentUrl(List<String> pathSegments) {
        if (!FirebaseConfig.isConfigured()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available.");
        }
        if (pathSegments == null || pathSegments.isEmpty()) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segments cannot be empty.");
        }
        String baseUrl = FirebaseConfig.getFirestoreDocumentsUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore base URL is not configured.");
        }
        StringBuilder pathBuilder = new StringBuilder(baseUrl);
        for (String segment : pathSegments) {
            OperationResult<Void> segCheck = validatePathSegment(segment);
            if (segCheck.isFailure()) {
                return OperationResult.failure(segCheck.getErrorCode(), segCheck.getMessage());
            }
            pathBuilder.append("/").append(URLEncoder.encode(segment.trim(), StandardCharsets.UTF_8));
        }
        return OperationResult.success(pathBuilder.toString());
    }

    public CompletableFuture<OperationResult<JSONObject>> getDocumentBySegments(List<String> pathSegments,
            String idToken) {
        OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
        if (urlRes.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRes.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                            "Failed to create getDocumentBySegments request."));
        }
    }

    public CompletableFuture<OperationResult<JSONObject>> getPublicDocumentBySegments(List<String> pathSegments) {
        OperationResult<String> urlRes = validateAndBuildPublicSegmentUrl(pathSegments);
        if (urlRes.isFailure()) {
            return CompletableFuture
                    .completedFuture(OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRes.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                    "Failed to create getPublicDocumentBySegments request."));
        }
    }

    public CompletableFuture<OperationResult<JSONObject>> setDocumentBySegments(List<String> pathSegments,
            JSONObject document, String idToken) {
        OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
        if (urlRes.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
        }
        if (document == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRes.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                            "Failed to create setDocumentBySegments request."));
        }
    }

    public CompletableFuture<OperationResult<List<JSONObject>>> listDocumentsBySegments(List<String> pathSegments,
            String idToken) {
        OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
        if (urlRes.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRes.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            try {
                                String body = response.body();
                                List<JSONObject> documents = new ArrayList<>();
                                if (body != null && !body.isBlank()) {
                                    JSONObject json = new JSONObject(body);
                                    if (json.has("documents")) {
                                        JSONArray array = json.getJSONArray("documents");
                                        for (int i = 0; i < array.length(); i++) {
                                            documents.add(array.getJSONObject(i));
                                        }
                                    }
                                }
                                return OperationResult.<List<JSONObject>>success(documents);
                            } catch (Exception e) {
                                return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
                                        "Failed to parse Firestore list response.");
                            }
                        } else if (statusCode == 404) {
                            return OperationResult.<List<JSONObject>>success(new ArrayList<>());
                        } else {
                            return mapHttpErrorToListResult(statusCode);
                        }
                    })
                    .exceptionally(this::handleTransportErrorList);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                            "Failed to create listDocumentsBySegments request."));
        }
    }

    public CompletableFuture<OperationResult<List<JSONObject>>> listPublicDocumentsBySegments(
            List<String> pathSegments) {
        OperationResult<String> urlRes = validateAndBuildPublicSegmentUrl(pathSegments);
        if (urlRes.isFailure()) {
            return CompletableFuture
                    .completedFuture(OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRes.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            try {
                                String body = response.body();
                                List<JSONObject> documents = new ArrayList<>();
                                if (body != null && !body.isBlank()) {
                                    JSONObject json = new JSONObject(body);
                                    if (json.has("documents")) {
                                        JSONArray array = json.getJSONArray("documents");
                                        for (int i = 0; i < array.length(); i++) {
                                            documents.add(array.getJSONObject(i));
                                        }
                                    }
                                }
                                return OperationResult.<List<JSONObject>>success(documents);
                            } catch (Exception e) {
                                return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
                                        "Failed to parse public list response.");
                            }
                        } else if (statusCode == 404) {
                            return OperationResult.<List<JSONObject>>success(new ArrayList<>());
                        } else {
                            return mapHttpErrorToListResult(statusCode);
                        }
                    }).exceptionally(this::handleTransportErrorList);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                    "Failed to create listPublicDocuments request."));
        }
    }

    public CompletableFuture<OperationResult<Void>> deleteDocumentBySegments(List<String> pathSegments,
            String idToken) {
        OperationResult<String> urlRes = validateAndBuildSegmentUrl(pathSegments, idToken);
        if (urlRes.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(urlRes.getErrorCode(), urlRes.getMessage()));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRes.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Accept", "application/json")
                    .DELETE()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            return OperationResult.<Void>success(null, "Document deleted successfully.");
                        } else if (statusCode == 404) {
                            // Removing an absent favorite should produce a stable success/not-found result
                            return OperationResult.<Void>success(null, "Document already deleted or not found.");
                        } else {
                            return mapHttpErrorToVoidResult(statusCode, response.body());
                        }
                    })
                    .exceptionally(this::handleTransportErrorVoid);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                            "Failed to create deleteDocumentBySegments request."));
        }
    }

    private OperationResult<Void> validatePathSegment(String segment) {
        if (segment == null || segment.isBlank()) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segment cannot be null or blank.");
        }
        String trimmed = segment.trim();
        if (trimmed.contains("/") || trimmed.contains("\\") || trimmed.contains("..")
                || trimmed.contains("?") || trimmed.contains("#") || trimmed.contains("%")) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path segment contains invalid characters.");
        }
        return OperationResult.success(null);
    }

    public CompletableFuture<OperationResult<JSONObject>> getNestedDocument(
            String parentCollection,
            String parentDocumentId,
            String childCollection,
            String childDocumentId,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildNestedUrl(
                parentCollection, parentDocumentId, childCollection, childDocumentId, idToken);

        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pathResult.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create getNestedDocument request."));
        }
    }

    public CompletableFuture<OperationResult<JSONObject>> setNestedDocument(
            String parentCollection,
            String parentDocumentId,
            String childCollection,
            String childDocumentId,
            JSONObject document,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildNestedUrl(
                parentCollection, parentDocumentId, childCollection, childDocumentId, idToken);

        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }
        if (document == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pathResult.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create setNestedDocument request."));
        }
    }

    public CompletableFuture<OperationResult<JSONObject>> updateNestedDocument(
            String parentCollection,
            String parentDocumentId,
            String childCollection,
            String childDocumentId,
            JSONObject document,
            List<String> updateFields,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildNestedUrl(
                parentCollection, parentDocumentId, childCollection, childDocumentId, idToken);

        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }
        if (document == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_INVALID_DOCUMENT", "Document content cannot be null."));
        }

        try {
            StringBuilder urlBuilder = new StringBuilder(pathResult.getData());
            if (updateFields != null && !updateFields.isEmpty()) {
                boolean first = true;
                for (String field : updateFields) {
                    if (field != null && !field.isBlank()) {
                        urlBuilder.append(first ? "?" : "&")
                                .append("updateMask.fieldPaths=")
                                .append(URLEncoder.encode(field.trim(), StandardCharsets.UTF_8));
                        first = false;
                    }
                }
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()))
                    .build();

            return executeJsonObjectRequest(request);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                            "Failed to create updateNestedDocument request."));
        }
    }

    public CompletableFuture<OperationResult<List<JSONObject>>> listNestedDocuments(
            String parentCollection,
            String parentDocumentId,
            String childCollection,
            String idToken) {

        OperationResult<String> pathResult = validateAndBuildNestedUrl(
                parentCollection, parentDocumentId, childCollection, null, idToken);

        if (pathResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(pathResult.getErrorCode(), pathResult.getMessage()));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pathResult.getData()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            try {
                                String body = response.body();
                                List<JSONObject> documents = new ArrayList<>();
                                if (body != null && !body.isBlank()) {
                                    JSONObject json = new JSONObject(body);
                                    if (json.has("documents")) {
                                        JSONArray array = json.getJSONArray("documents");
                                        for (int i = 0; i < array.length(); i++) {
                                            documents.add(array.getJSONObject(i));
                                        }
                                    }
                                }
                                return OperationResult.<List<JSONObject>>success(documents);
                            } catch (Exception e) {
                                return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
                                        "Failed to parse Firestore list response.");
                            }
                        } else if (statusCode == 404) {
                            return OperationResult.<List<JSONObject>>success(new ArrayList<>());
                        } else {
                            return mapHttpErrorToListResult(statusCode);
                        }
                    })
                    .exceptionally(this::handleTransportErrorList);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED",
                            "Failed to create listNestedDocuments request."));
        }
    }

    private CompletableFuture<OperationResult<JSONObject>> executeJsonObjectRequest(HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int statusCode = response.statusCode();
                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            String body = response.body();
                            JSONObject json = (body != null && !body.isBlank()) ? new JSONObject(body)
                                    : new JSONObject();
                            return OperationResult.success(json);
                        } catch (Exception e) {
                            return OperationResult.<JSONObject>failure(OperationCode.UNKNOWN_ERROR,
                                    "Failed to parse Firestore JSON response.");
                        }
                    } else {
                        return mapHttpErrorToJsonResult(statusCode, response.body());
                    }
                })
                .exceptionally(this::handleTransportErrorJson);
    }

    private OperationResult<JSONObject> mapHttpErrorToJsonResult(int statusCode) {
        return mapHttpErrorToJsonResult(statusCode, null);
    }

    private OperationResult<Void> mapHttpErrorToVoidResult(int statusCode) {
        return mapHttpErrorToVoidResult(statusCode, null);
    }

    private OperationResult<JSONObject> mapHttpErrorToJsonResult(int statusCode, String responseBody) {
        String detail = extractSanitizedFirebaseError(statusCode, responseBody);
        switch (statusCode) {
            case 400:
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid argument: " + detail);
            case 401:
                return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                        "Authentication required: " + detail);
            case 403:
                return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied: " + detail);
            case 404:
                return OperationResult.failure(OperationCode.NOT_FOUND, "Not found: " + detail);
            case 429:
                return OperationResult.failure(OperationCode.NETWORK_ERROR, "Too many requests: " + detail);
            default:
                return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore error: " + detail);
        }
    }

    private OperationResult<Void> mapHttpErrorToVoidResult(int statusCode, String responseBody) {
        String detail = extractSanitizedFirebaseError(statusCode, responseBody);
        switch (statusCode) {
            case 400:
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid argument: " + detail);
            case 401:
                return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                        "Authentication required: " + detail);
            case 403:
                return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied: " + detail);
            case 404:
                return OperationResult.failure(OperationCode.NOT_FOUND, "Not found: " + detail);
            case 429:
                return OperationResult.failure(OperationCode.NETWORK_ERROR, "Too many requests: " + detail);
            default:
                return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firestore error: " + detail);
        }
    }

    private String extractSanitizedFirebaseError(int statusCode, String responseBody) {
        if (responseBody != null && !responseBody.isBlank()) {
            try {
                JSONObject json = new JSONObject(responseBody);
                if (json.has("error")) {
                    JSONObject err = json.getJSONObject("error");
                    String status = err.optString("status", "");
                    String message = err.optString("message", "");
                    if (!message.isBlank()) {
                        message = message.replaceAll("Bearer\\s+[A-Za-z0-9._-]+", "[REDACTED_TOKEN]");
                        return (!status.isBlank() ? status + " — " : "") + message + " (HTTP " + statusCode + ")";
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return "HTTP " + statusCode;
    }

    private OperationResult<JSONObject> handleTransportErrorJson(Throwable throwable) {
        Throwable cause = (throwable.getCause() != null) ? throwable.getCause() : throwable;
        if (cause instanceof HttpTimeoutException) {
            return OperationResult.failure(OperationCode.NETWORK_ERROR, "The request to Firestore timed out.");
        } else if (cause instanceof java.net.ConnectException) {
            return OperationResult.failure(OperationCode.NETWORK_ERROR, "Could not connect to Firestore service.");
        } else if (cause instanceof InterruptedException) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "The operation was interrupted.");
        } else {
            return OperationResult.failure(OperationCode.NETWORK_ERROR,
                    "Network error occurred while communicating with Firestore.");
        }
    }

    private OperationResult<Void> handleTransportErrorVoid(Throwable throwable) {
        Throwable cause = (throwable.getCause() != null) ? throwable.getCause() : throwable;
        if (cause instanceof HttpTimeoutException) {
            return OperationResult.failure(OperationCode.NETWORK_ERROR, "The request to Firestore timed out.");
        } else if (cause instanceof java.net.ConnectException) {
            return OperationResult.failure(OperationCode.NETWORK_ERROR, "Could not connect to Firestore service.");
        } else if (cause instanceof InterruptedException) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "The operation was interrupted.");
        } else {
            return OperationResult.failure(OperationCode.NETWORK_ERROR,
                    "Network error occurred while communicating with Firestore.");
        }
    }

    public CompletableFuture<OperationResult<List<JSONObject>>> queryDocuments(
            String collectionName,
            String fieldName,
            Object equalValue,
            String idToken) {

        if (fieldName == null || fieldName.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Field name cannot be blank."));
        }
        Map<String, Object> filters = new java.util.LinkedHashMap<>();
        filters.put(fieldName, equalValue);
        return queryDocuments(collectionName, filters, idToken);
    }

    public CompletableFuture<OperationResult<List<JSONObject>>> queryDocuments(
            String collectionName,
            Map<String, Object> equalityFilters,
            String idToken) {

        if (!FirebaseConfig.isConfigured()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required for query."));
        }
        if (collectionName == null || collectionName.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Collection name cannot be blank."));
        }
        if (equalityFilters == null || equalityFilters.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Equality filters map cannot be empty."));
        }

        List<JSONObject> fieldFilters = new ArrayList<>();
        for (Map.Entry<String, Object> entry : equalityFilters.entrySet()) {
            String fName = entry.getKey();
            if (fName == null || fName.isBlank()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Filter key cannot be blank."));
            }
            Object val = entry.getValue();
            JSONObject valueObj;
            if (val instanceof Boolean) {
                valueObj = FirestoreDocumentUtil.booleanValue((Boolean) val);
            } else if (val instanceof Number) {
                valueObj = FirestoreDocumentUtil.integerValue(((Number) val).longValue());
            } else {
                valueObj = FirestoreDocumentUtil.stringValue(val != null ? val.toString() : "");
            }
            JSONObject fieldFilter = new JSONObject()
                    .put("field", new JSONObject().put("fieldPath", fName.trim()))
                    .put("op", "EQUAL")
                    .put("value", valueObj);
            fieldFilters.add(new JSONObject().put("fieldFilter", fieldFilter));
        }

        JSONObject whereObj;
        if (fieldFilters.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Equality filters map cannot be empty."));
        } else if (fieldFilters.size() == 1) {
            whereObj = fieldFilters.get(0);
        } else {
            JSONArray filtersArray = new JSONArray();
            for (JSONObject ff : fieldFilters) {
                filtersArray.put(ff);
            }
            JSONObject compositeFilter = new JSONObject()
                    .put("op", "AND")
                    .put("filters", filtersArray);
            whereObj = new JSONObject().put("compositeFilter", compositeFilter);
        }

        JSONArray fromArray = new JSONArray()
                .put(new JSONObject().put("collectionId", collectionName.trim()));

        JSONObject structuredQuery = new JSONObject()
                .put("from", fromArray)
                .put("where", whereObj)
                // Firestore rules cap authenticated collection queries at 100.
                // Sending the same bound is required; an omitted limit is rejected.
                .put("limit", 100);

        JSONObject payload = new JSONObject().put("structuredQuery", structuredQuery);

        String runQueryUrl = FirebaseConfig.getFirestoreDocumentsUrl() + ":runQuery";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(runQueryUrl))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + idToken.trim())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int statusCode = response.statusCode();
                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            String body = response.body();
                            List<JSONObject> documents = new ArrayList<>();
                            if (body != null && !body.isBlank()) {
                                JSONArray array = new JSONArray(body);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject item = array.optJSONObject(i);
                                    if (item != null && item.has("document")) {
                                        documents.add(item.getJSONObject("document"));
                                    }
                                }
                            }
                            return OperationResult.success(documents);
                        } catch (Exception e) {
                            return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
                                    "Failed to parse Firestore query response.");
                        }
                    } else {
                        return mapHttpErrorToListResult(statusCode);
                    }
                })
                .exceptionally(this::handleTransportErrorList);
    }

    public CompletableFuture<OperationResult<List<JSONObject>>> publicQueryDocuments(
            String collectionName,
            Map<String, Object> equalityFilters) {

        if (!FirebaseConfig.isConfigured()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Firebase configuration is not available."));
        }
        if (collectionName == null || collectionName.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Collection name cannot be blank."));
        }
        if (equalityFilters == null || equalityFilters.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Equality filters map cannot be empty."));
        }

        List<JSONObject> fieldFilters = new ArrayList<>();
        for (Map.Entry<String, Object> entry : equalityFilters.entrySet()) {
            String fName = entry.getKey();
            if (fName == null || fName.isBlank()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Filter key cannot be blank."));
            }
            Object val = entry.getValue();
            JSONObject valueObj;
            if (val instanceof Boolean) {
                valueObj = FirestoreDocumentUtil.booleanValue((Boolean) val);
            } else if (val instanceof Number) {
                valueObj = FirestoreDocumentUtil.integerValue(((Number) val).longValue());
            } else {
                valueObj = FirestoreDocumentUtil.stringValue(val != null ? val.toString() : "");
            }
            JSONObject fieldFilter = new JSONObject()
                    .put("field", new JSONObject().put("fieldPath", fName.trim()))
                    .put("op", "EQUAL")
                    .put("value", valueObj);
            fieldFilters.add(new JSONObject().put("fieldFilter", fieldFilter));
        }

        JSONObject whereObj;
        if (fieldFilters.size() == 1) {
            whereObj = fieldFilters.get(0);
        } else {
            JSONArray filtersArray = new JSONArray();
            for (JSONObject ff : fieldFilters) {
                filtersArray.put(ff);
            }
            JSONObject compositeFilter = new JSONObject()
                    .put("op", "AND")
                    .put("filters", filtersArray);
            whereObj = new JSONObject().put("compositeFilter", compositeFilter);
        }

        JSONArray fromArray = new JSONArray()
                .put(new JSONObject().put("collectionId", collectionName.trim()));

        JSONObject structuredQuery = new JSONObject()
                .put("from", fromArray)
                .put("where", whereObj);

        JSONObject payload = new JSONObject().put("structuredQuery", structuredQuery);

        String runQueryUrl = FirebaseConfig.getFirestoreDocumentsUrl() + ":runQuery";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(runQueryUrl))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int statusCode = response.statusCode();
                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            String body = response.body();
                            List<JSONObject> documents = new ArrayList<>();
                            if (body != null && !body.isBlank()) {
                                JSONArray array = new JSONArray(body);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject item = array.optJSONObject(i);
                                    if (item != null && item.has("document")) {
                                        documents.add(item.getJSONObject("document"));
                                    }
                                }
                            }
                            return OperationResult.success(documents);
                        } catch (Exception e) {
                            return OperationResult.<List<JSONObject>>failure(OperationCode.UNKNOWN_ERROR,
                                    "Failed to parse public query response.");
                        }
                    } else {
                        return mapHttpErrorToListResult(statusCode);
                    }
                })
                .exceptionally(this::handleTransportErrorList);
    }

    private OperationResult<List<JSONObject>> mapHttpErrorToListResult(int statusCode) {
        switch (statusCode) {
            case 400:
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid argument (HTTP 400).");
            case 401:
                return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                        "Authentication required. Token may be expired (HTTP 401).");
            case 403:
                return OperationResult.failure(OperationCode.PERMISSION_DENIED,
                        "Access denied. Insufficient permissions (HTTP 403).");
            case 404:
                return OperationResult.failure(OperationCode.NOT_FOUND,
                        "The requested resource was not found (HTTP 404).");
            case 429:
                return OperationResult.failure(OperationCode.NETWORK_ERROR,
                        "Too many requests. Please try again later (HTTP 429).");
            case 500:
            case 502:
            case 503:
            case 504:
                return OperationResult.failure(OperationCode.NETWORK_ERROR,
                        "Firestore service is temporarily unavailable (HTTP " + statusCode + ").");
            default:
                return OperationResult.failure(OperationCode.UNKNOWN_ERROR,
                        "Firestore operation failed (HTTP " + statusCode + ").");
        }
    }

    public CompletableFuture<OperationResult<JSONObject>> commitBatchWrites(
            JSONArray writes,
            String idToken) {

        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                            "Authentication token is required for batch commit."));
        }
        if (writes == null || writes.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Batch writes payload cannot be empty."));
        }

        String commitUrl = FirebaseConfig.getFirestoreDocumentsUrl() + ":commit";

        JSONObject payload = new JSONObject().put("writes", writes);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(commitUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + idToken.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            try {
                                String body = response.body();
                                JSONObject json = (body != null && !body.isBlank()) ? new JSONObject(body)
                                        : new JSONObject();
                                return OperationResult.success(json);
                            } catch (Exception e) {
                                return OperationResult.<JSONObject>failure(OperationCode.UNKNOWN_ERROR,
                                        "Failed to parse Firestore batch commit response.");
                            }
                        } else {
                            return mapHttpErrorToJsonResult(statusCode, response.body());
                        }
                    })
                    .exceptionally(this::handleTransportErrorJson);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("FIRESTORE_REQUEST_FAILED", "Failed to create batch commit request."));
        }
    }

    private OperationResult<List<JSONObject>> handleTransportErrorList(Throwable throwable) {
        Throwable cause = (throwable.getCause() != null) ? throwable.getCause() : throwable;
        if (cause instanceof HttpTimeoutException) {
            return OperationResult.failure(OperationCode.NETWORK_ERROR, "The request to Firestore timed out.");
        } else if (cause instanceof java.net.ConnectException) {
            return OperationResult.failure(OperationCode.NETWORK_ERROR, "Could not connect to Firestore service.");
        } else if (cause instanceof InterruptedException) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "The operation was interrupted.");
        } else {
            return OperationResult.failure(OperationCode.NETWORK_ERROR,
                    "Network error occurred while communicating with Firestore.");
        }
    }
}