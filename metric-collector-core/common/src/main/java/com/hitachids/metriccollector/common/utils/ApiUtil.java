package com.hitachids.metriccollector.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for making HTTP requests and constructing API URIs.
 */
public class ApiUtil {
    private static final Log LOG = LogFactory.getLog(ApiUtil.class);
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    private ApiUtil() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    /**
     * Constructs the full URI for a given API endpoint.
     *
     * @param endpoint The API endpoint to append to the base URL
     * @return The full URI as a string
     * @throws Exception If the URI construction fails
     */
    public static String getURI(String endpoint) throws Exception {
        try {
            return ConfigurationUtil.getUrl(endpoint);
        } catch (Exception e) {
            String metricCollectorId = "unknown_unknown"; // Fallback ID
            String logMessage = formatLog("ERROR", metricCollectorId, "Failed to create URI for endpoint: " + endpoint + ": " + e.getMessage());
            LOG.error(logMessage, e);
            throw new Exception("Invalid URI for endpoint: " + endpoint, e);
        }
    }

    /**
     * Sends a POST request to the specified URI.
     *
     * @param uri The URI to send the request to
     * @param requestBody The request body as a JSON string
     * @param authHeader The authorization header
     * @param timeout The request timeout in milliseconds
     * @return The response as a JSON ObjectNode
     * @throws Exception If the request fails
     */
    public static ObjectNode post(String uri, String requestBody, String authHeader, int timeout) throws Exception {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            System.out.println(client);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(timeout))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), ObjectNode.class);
        } catch (Exception e) {
            System.err.println("Error in ApiUtil.post: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a GET request to the specified URI.
     *
     * @param uri The URI to send the request to
     * @param authHeader The authorization header
     * @param timeout The request timeout in milliseconds
     * @return The response as a JSON ObjectNode
     * @throws Exception If the request fails
     */
    public static ObjectNode get(String uri, String authHeader, int timeout) throws Exception {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(timeout))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), ObjectNode.class);
        } catch (Exception e) {
            System.err.println("Error in ApiUtil.get: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private static String formatLog(String level, String metricCollectorId, String message) {
        return String.format("%s [%s] %s ApiUtil: %s",
                ZonedDateTime.now().format(LOG_DATE_FORMAT),
                level,
                metricCollectorId,
                message);
    }
}