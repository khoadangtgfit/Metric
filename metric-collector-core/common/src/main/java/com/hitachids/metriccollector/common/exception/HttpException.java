package com.hitachids.metriccollector.common.exception;

public class HttpException extends RuntimeException {
    private final String statusCode;
    private final String retryAfter;

    public HttpException(String message) {
        this(message, null, null);
    }

    public HttpException(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    public HttpException(String message, String statusCode, String retryAfter) {
        this(message, statusCode, retryAfter, null);
    }

    public HttpException(String message, String statusCode, String retryAfter, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.retryAfter = retryAfter;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getRetryAfter() {
        return retryAfter;
    }
}