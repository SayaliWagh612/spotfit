package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.model.OperationResult;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FirestoreRestClientTest {

    private HttpClient mockHttpClient;
    private FirestoreRestClient restClient;

    @BeforeEach
    public void setUp() {
        mockHttpClient = mock(HttpClient.class);
        restClient = new FirestoreRestClient(mockHttpClient);
    }

    @Test
    @DisplayName("getPublicManagerInvitation sends GET request without Authorization header")
    public void testGetPublicManagerInvitation_OmitsAuthorizationHeader() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"fields\":{\"status\":{\"stringValue\":\"PENDING\"}}}");

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenAnswer(invocation -> {
                    HttpRequest request = invocation.getArgument(0);
                    
                    // Verify Authorization header is NOT present
                    assertFalse(request.headers().firstValue("Authorization").isPresent(),
                            "Public invitation GET must NOT contain an Authorization header.");
                    
                    // Verify GET method
                    assertEquals("GET", request.method(), "Request method must be GET.");
                    
                    // Verify URI targets managerInvitations collection
                    String uriString = request.uri().toString();
                    assertTrue(uriString.contains("/managerInvitations/CODE123"),
                            "Target URI must point directly to /managerInvitations/{code}.");
                    
                    return CompletableFuture.completedFuture(mockResponse);
                });

        CompletableFuture<OperationResult<JSONObject>> future = restClient.getPublicManagerInvitation("CODE123");
        OperationResult<JSONObject> result = future.get();

        assertTrue(result.isSuccess(), "Operation should succeed.");
        assertNotNull(result.getData(), "Response JSON should not be null.");
        verify(mockHttpClient, times(1)).sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    @DisplayName("getPublicManagerInvitation rejects blank invitation code locally")
    public void testGetPublicManagerInvitation_RejectsBlankCode() throws Exception {
        CompletableFuture<OperationResult<JSONObject>> future = restClient.getPublicManagerInvitation("   ");
        OperationResult<JSONObject> result = future.get();

        assertTrue(result.isFailure());
        assertEquals("INVALID_ARGUMENT", result.getErrorCode());
        verifyNoInteractions(mockHttpClient);
    }

    @Test
    @DisplayName("queryDocuments rejects empty equality filters map")
    public void testQueryDocuments_RejectsEmptyFilters() throws Exception {
        CompletableFuture<OperationResult<java.util.List<JSONObject>>> future = restClient.queryDocuments("spaces", java.util.Collections.emptyMap(), "token123");
        OperationResult<java.util.List<JSONObject>> result = future.get();

        assertTrue(result.isFailure());
        assertEquals("INVALID_ARGUMENT", result.getErrorCode());
        verifyNoInteractions(mockHttpClient);
    }

    @Test
    @DisplayName("queryDocuments constructs composite AND filter for multi-filter queries")
    public void testQueryDocuments_ConstructsCompositeAndFilter() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("[{\"document\":{\"name\":\"projects/spotfit/databases/(default)/documents/spaces/s1\"}}]");

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenAnswer(invocation -> {
                    HttpRequest request = invocation.getArgument(0);
                    assertEquals("POST", request.method());
                    assertTrue(request.uri().toString().endsWith(":runQuery"));

                    // Verify Authorization header
                    assertTrue(request.headers().firstValue("Authorization").isPresent());
                    assertEquals("Bearer token123", request.headers().firstValue("Authorization").get());

                    return CompletableFuture.completedFuture(mockResponse);
                });

        java.util.Map<String, Object> filters = new java.util.LinkedHashMap<>();
        filters.put("status", "LIVE");
        filters.put("active", Boolean.TRUE);
        filters.put("category", "CREATIVITY");
        filters.put("activityGroup", "ART");

        CompletableFuture<OperationResult<java.util.List<JSONObject>>> future = restClient.queryDocuments("spaces", filters, "token123");
        OperationResult<java.util.List<JSONObject>> result = future.get();

        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(mockHttpClient, times(1)).sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
