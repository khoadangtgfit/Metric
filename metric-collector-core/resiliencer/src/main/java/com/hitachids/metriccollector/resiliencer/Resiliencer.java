package com.hitachids.metriccollector.resiliencer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class Resiliencer {
    private static final Log LOG = LogFactory.getLog(Resiliencer.class);
    private static final Random JITTER = new Random();
    private static final int BASE_RETRY_INTERVAL_MS = 1000;

    private final int maxRetries;

    public Resiliencer(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    public <T> T executeWithRetry(SupplierWithException<T> supplier, String operationName) throws Exception {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < maxRetries) {
            try {
                return supplier.get();
            } catch (Exception ex) {
                lastException = ex;
                String statusCode = extractStatusCode(ex);
                if (!isRetryableError(statusCode)) {
                    throw ex;
                }

                long delay = calculateExponentialBackoff(retryCount);
                LOG.warn(String.format("Retry attempt %d after %dms for %s due to: %s",
                        retryCount + 1, delay, operationName, ex.getMessage()));
                Thread.sleep(delay);
                retryCount++;
            }
        }

        LOG.error(String.format("Failed after %d retries for %s: %s",
                maxRetries, operationName, lastException.getMessage()), lastException);
        throw lastException;
    }

    private boolean isRetryableError(String statusCode) {
        return statusCode != null && (
                "408".equals(statusCode) || // Request Timeout
                        "429".equals(statusCode) || // Too Many Requests
                        "502".equals(statusCode) || // Bad Gateway
                        "504".equals(statusCode)    // Gateway Timeout
        );
    }

    private String extractStatusCode(Exception ex) {
        return ex.getMessage() != null && ex.getMessage().matches("\\d{3}")
                ? ex.getMessage()
                : null;
    }

    private long calculateExponentialBackoff(int retryCount) {
        long baseDelay = BASE_RETRY_INTERVAL_MS * (1L << retryCount);
        long jitter = JITTER.nextInt(100);
        return Math.min(baseDelay + jitter, 10000); // Max 10 seconds
    }
}