package com.hitachids.metriccollector.common.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.hitachids.metriccollector.common.exception.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApiUtil {
    private static final Log LOG = LogFactory.getLog(ApiUtil.class);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ApiUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static URI getURI(String endpoint) {
        try {
            String url = ConfigurationUtil.getUrl(endpoint);
            return new URI(url);
        } catch (Exception e) {
            LOG.error("Failed to create URI for endpoint: " + endpoint, e);
            throw new HttpException("Invalid URI: " + endpoint, e);
        }
    }

    public static ObjectNode post(String uri, String body, String authHeader, int timeoutMs) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(timeoutMs))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return OBJECT_MAPPER.readValue(response.body(), ObjectNode.class);
            } else {
                String retryAfter = response.headers().firstValue("Retry-After").orElse(null);
                throw new HttpException("HTTP POST failed with status: " + statusCode, String.valueOf(statusCode), retryAfter);
            }
        } catch (Exception e) {
            LOG.error("POST request failed for URI: " + uri, e);
            throw new HttpException("POST request failed: " + uri, e);
        }
    }

    public static ObjectNode get(String uri, String authHeader, int timeoutMs) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Authorization", authHeader)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return OBJECT_MAPPER.readValue(response.body(), ObjectNode.class);
            } else {
                String retryAfter = response.headers().firstValue("Retry-After").orElse(null);
                throw new HttpException("HTTP GET failed with status: " + statusCode, String.valueOf(statusCode), retryAfter);
            }
        } catch (Exception e) {
            LOG.error("GET request failed for URI: " + uri, e);
            throw new HttpException("GET request failed: " + uri, e);
        }
    }
}