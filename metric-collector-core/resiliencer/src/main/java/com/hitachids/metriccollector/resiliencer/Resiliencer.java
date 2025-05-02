package com.hitachids.metriccollector.resiliencer;

import com.hitachids.metriccollector.common.exception.HttpException;
import com.hitachids.metriccollector.common.utils.ConfigurationUtil;
import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Resiliencer {
    private static final Log LOG = LogFactory.getLog(Resiliencer.class);
    private static final int MAX_RETRIES = ConfigurationUtil.getMaxRetries(3);
    private static final int MAX_JITTER_MS = 1000; // Random jitter up to 1000ms
    private static final long MAX_BACKOFF_MS = 10000; // 10 seconds
    private static final Random JITTER = new Random();

    public <T> T executeWithRetry(SupplierWithException<T> supplier) throws Exception {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRIES) {
            try {
                return supplier.get();
            } catch (Exception ex) {
                lastException = ex;
                String statusCode = extractStatusCode(ex);
                if (!isRetryableError(statusCode)) {
                    throw ex;
                }

                long delay = calculateRetryDelay(retryCount, ex);
                LOG.warn("Retry attempt " + (retryCount + 1) + " after " + delay + "ms due to: " + ex.getMessage());
                Thread.sleep(delay);
                retryCount++;
            }
        }

        LOG.error("Failed after " + MAX_RETRIES + " retries", lastException);
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
        return ex instanceof HttpException ? ((HttpException) ex).getStatusCode() : null;
    }

    private long calculateRetryDelay(int retryCount, Exception ex) {
        if (ex instanceof HttpException) {
            String retryAfter = ((HttpException) ex).getRetryAfter();
            if (retryAfter != null) {
                try {
                    return Long.parseLong(retryAfter) * 1000; // Convert seconds to milliseconds
                } catch (NumberFormatException e) {
                    LOG.warn("Invalid Retry-After header: " + retryAfter + ", falling back to exponential backoff");
                }
            }
        }

        // Calculate delay: (2^n seconds * 1000) + random jitter (0-1000ms)
        long baseDelaySeconds = 1L << retryCount; // 2^n seconds (1, 2, 4, ...)
        long baseDelayMs = baseDelaySeconds * 1000; // Convert to milliseconds
        long jitterMs = JITTER.nextInt(MAX_JITTER_MS + 1); // Random 0-1000ms
        return Math.min(baseDelayMs + jitterMs, MAX_BACKOFF_MS); // Cap at 10 seconds
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}