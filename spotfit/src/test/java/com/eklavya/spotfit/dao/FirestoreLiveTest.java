package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.dao.AuthDao;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.config.FirebaseConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FirestoreLiveTest {

    @Test
    public void testFirestorePermissions() throws Exception {
        AuthDao authDao = new AuthDao();
        
        // Try to sign in as customer
        AuthResult auth = authDao.signIn("customer@example.com", "customer123").join();
        if (auth.isFailure()) {
            System.out.println("Failed to sign in: " + auth.getMessage());
            return;
        }
        
        System.out.println("Signed in successfully as " + auth.getUserId());
        String token = auth.getIdToken();
        String projectId = "spotfit-fb3b6"; // User mentioned this
        String baseUrl = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents";

        HttpClient client = HttpClient.newHttpClient();

        // 1. Direct GET of the known space
        String spaceId = "c058b1a3-499a-4132-9984-8067118e8643";
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/spaces/" + spaceId))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        
        HttpResponse<String> getResp = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("\n--- DIRECT GET RESPONSE ---");
        System.out.println("Status: " + getResp.statusCode());
        System.out.println("Body: " + getResp.body());

        // 2. LIST Query using runQuery
        JSONObject runQueryJson = new JSONObject();
        JSONObject structuredQuery = new JSONObject();
        
        JSONArray from = new JSONArray();
        JSONObject collection = new JSONObject();
        collection.put("collectionId", "spaces");
        from.put(collection);
        structuredQuery.put("from", from);

        JSONObject where = new JSONObject();
        JSONObject compositeFilter = new JSONObject();
        compositeFilter.put("op", "AND");
        JSONArray filters = new JSONArray();

        filters.put(buildEqualityFilter("schemaVersion", 2, "integerValue"));
        filters.put(buildEqualityFilter("approvalStatus", "APPROVED", "stringValue"));
        filters.put(buildEqualityFilter("operationalStatus", "ACTIVE", "stringValue"));
        filters.put(buildEqualityFilter("active", true, "booleanValue"));
        filters.put(buildEqualityFilter("category", "CREATIVITY", "stringValue"));
        // The user also mentioned MUSIC as the activity group, but we are just querying the 5 fields + 1 for activityGroup
        filters.put(buildEqualityFilter("activityGroup", "MUSIC", "stringValue"));

        compositeFilter.put("filters", filters);
        where.put("compositeFilter", compositeFilter);
        structuredQuery.put("where", where);
        runQueryJson.put("structuredQuery", structuredQuery);

        System.out.println("\n--- RUN QUERY REQUEST JSON ---");
        System.out.println(runQueryJson.toString(2));

        HttpRequest queryRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + ":runQuery"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(runQueryJson.toString()))
                .build();
        
        HttpResponse<String> queryResp = client.send(queryRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("\n--- RUN QUERY RESPONSE ---");
        System.out.println("Status: " + queryResp.statusCode());
        System.out.println("Body: " + queryResp.body());
    }

    private static JSONObject buildEqualityFilter(String fieldPath, Object value, String valueType) {
        JSONObject filter = new JSONObject();
        JSONObject fieldFilter = new JSONObject();
        
        JSONObject field = new JSONObject();
        field.put("fieldPath", fieldPath);
        fieldFilter.put("field", field);
        fieldFilter.put("op", "EQUAL");
        
        JSONObject val = new JSONObject();
        val.put(valueType, value);
        fieldFilter.put("value", val);
        
        filter.put("fieldFilter", fieldFilter);
        return filter;
    }
}
