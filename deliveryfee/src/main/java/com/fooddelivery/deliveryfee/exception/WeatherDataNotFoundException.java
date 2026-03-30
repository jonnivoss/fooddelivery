package com.fooddelivery.deliveryfee.exception;

/**
 * Thrown when no weather observation data is available for the requested city.
 *
 * <p>This can occur if the scheduled weather import has not run yet
 * or if the external weather service was unavailable.
 */
public class WeatherDataNotFoundException extends RuntimeException {

    public WeatherDataNotFoundException(String city) {
        super("No weather data available for city: " + city);
    }
}
