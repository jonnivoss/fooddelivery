package com.fooddelivery.deliveryfee.dto;

import java.time.Instant;

/**
 * Standard error payload returned for all API error responses.
 *
 * @param timestamp ISO-8601 UTC timestamp of when the error occurred
 * @param status    HTTP status code
 * @param error     short HTTP status description
 * @param message   human-readable description of the error
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
) {

    /**
     * Convenience factory method.
     *
     * @param status  HTTP status code
     * @param error   short HTTP status description
     * @param message human-readable error detail
     * @return a new {@code ErrorResponse} stamped with the current UTC time
     */
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(Instant.now(), status, error, message);
    }
}
