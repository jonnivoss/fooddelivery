package com.fooddelivery.deliveryfee.exception;

/**
 * Thrown when a selected vehicle type is not permitted to operate
 * under the current weather conditions.
 *
 * <p>Triggers:
 * <ul>
 *   <li>Wind speed &gt; 20 m/s (bikes)</li>
 *   <li>Weather phenomenon is glaze, hail, or thunder (scooters and bikes)</li>
 * </ul>
 */
public class VehicleUsageForbiddenException extends RuntimeException {

    public VehicleUsageForbiddenException() {
        super("Usage of selected vehicle type is forbidden");
    }

    public VehicleUsageForbiddenException(String reason) {
        super("Usage of selected vehicle type is forbidden: " + reason);
    }
}
