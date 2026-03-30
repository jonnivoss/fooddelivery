package com.fooddelivery.deliveryfee.config;

import com.fooddelivery.deliveryfee.dto.ErrorResponse;
import com.fooddelivery.deliveryfee.exception.VehicleUsageForbiddenException;
import com.fooddelivery.deliveryfee.exception.WeatherDataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Centralised exception handling for all REST controllers.
 *
 * <p>Maps application and framework exceptions to consistent
 * {@link ErrorResponse} JSON payloads with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles attempts to use a vehicle type that is forbidden under current weather conditions.
     * Returns {@code 422 Unprocessable Entity}.
     */
    @ExceptionHandler(VehicleUsageForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleVehicleUsageForbidden(VehicleUsageForbiddenException ex) {
        log.warn("Vehicle usage forbidden: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(422, "Unprocessable Entity", ex.getMessage()));
    }

    /**
     * Handles missing weather data for a requested city.
     * Returns {@code 404 Not Found}.
     */
    @ExceptionHandler(WeatherDataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWeatherDataNotFound(WeatherDataNotFoundException ex) {
        log.warn("Weather data not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, "Not Found", ex.getMessage()));
    }

    /**
     * Handles missing required request parameters.
     * Returns {@code 400 Bad Request}.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = "Required parameter '" + ex.getParameterName() + "' is missing";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", message));
    }

    /**
     * Handles invalid enum values or type mismatches in request parameters.
     * Returns {@code 400 Bad Request}.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", message));
    }

    /**
     * Catch-all handler for unexpected exceptions.
     * Returns {@code 500 Internal Server Error}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Internal Server Error", "An unexpected error occurred"));
    }
}
