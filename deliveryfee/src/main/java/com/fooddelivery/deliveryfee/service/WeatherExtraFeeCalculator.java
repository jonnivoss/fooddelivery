package com.fooddelivery.deliveryfee.service;

import com.fooddelivery.deliveryfee.entity.VehicleType;
import com.fooddelivery.deliveryfee.entity.WeatherObservation;
import com.fooddelivery.deliveryfee.exception.VehicleUsageForbiddenException;
import org.springframework.stereotype.Service;

/**
 * Calculates weather-related extra fees for a given vehicle type and weather observation.
 *
 * <p>Three separate fee components are defined:
 * <ul>
 *   <li><b>ATEF</b> – Air Temperature Extra Fee (scooter and bike)</li>
 *   <li><b>WSEF</b> – Wind Speed Extra Fee (bike only)</li>
 *   <li><b>WPEF</b> – Weather Phenomenon Extra Fee (scooter and bike)</li>
 * </ul>
 */
@Service
public class WeatherExtraFeeCalculator {

    private static final double BELOW_MINUS_TEN_FEE = 1.00;
    private static final double BETWEEN_MINUS_TEN_AND_ZERO_FEE = 0.50;

    private static final double MODERATE_WIND_FEE = 0.50;
    private static final double MAX_ALLOWED_WIND_SPEED = 20.0;
    private static final double MODERATE_WIND_THRESHOLD = 10.0;

    private static final double SNOW_SLEET_PHENOMENON_FEE = 1.00;
    private static final double RAIN_PHENOMENON_FEE = 0.50;

    /**
     * Calculates the air temperature extra fee (ATEF).
     *
     * <p>Applies to scooters and bikes only.
     * <ul>
     *   <li>Temperature &lt; -10°C → 1.00 €</li>
     *   <li>-10°C ≤ temperature ≤ 0°C → 0.50 €</li>
     *   <li>Temperature &gt; 0°C → 0.00 €</li>
     * </ul>
     *
     * @param vehicleType  the courier's vehicle type
     * @param observation  the most recent weather observation for the city
     * @return temperature extra fee in euros
     */
    public double calculateTemperatureFee(VehicleType vehicleType, WeatherObservation observation) {
        if (!isWeatherSensitiveVehicle(vehicleType)) {
            return 0.0;
        }

        Double temperature = observation.getAirTemperature();
        if (temperature == null) {
            return 0.0;
        }

        if (temperature < -10.0) {
            return BELOW_MINUS_TEN_FEE;
        }
        if (temperature <= 0.0) {
            return BETWEEN_MINUS_TEN_AND_ZERO_FEE;
        }
        return 0.0;
    }

    /**
     * Calculates the wind speed extra fee (WSEF).
     *
     * <p>Applies to bikes only.
     * <ul>
     *   <li>Wind speed &gt; 20 m/s → {@link VehicleUsageForbiddenException}</li>
     *   <li>10 m/s ≤ wind speed ≤ 20 m/s → 0.50 €</li>
     *   <li>Wind speed &lt; 10 m/s → 0.00 €</li>
     * </ul>
     *
     * @param vehicleType  the courier's vehicle type
     * @param observation  the most recent weather observation for the city
     * @return wind speed extra fee in euros
     * @throws VehicleUsageForbiddenException if wind speed exceeds 20 m/s
     */
    public double calculateWindSpeedFee(VehicleType vehicleType, WeatherObservation observation) {
        if (vehicleType != VehicleType.BIKE) {
            return 0.0;
        }

        Double windSpeed = observation.getWindSpeed();
        if (windSpeed == null) {
            return 0.0;
        }

        if (windSpeed > MAX_ALLOWED_WIND_SPEED) {
            throw new VehicleUsageForbiddenException("Usage of selected vehicle type is forbidden");
        }
        if (windSpeed >= MODERATE_WIND_THRESHOLD) {
            return MODERATE_WIND_FEE;
        }
        return 0.0;
    }

    /**
     * Calculates the weather phenomenon extra fee (WPEF).
     *
     * <p>Applies to scooters and bikes only.
     * <ul>
     *   <li>Glaze, hail, or thunder → {@link VehicleUsageForbiddenException}</li>
     *   <li>Snow or sleet phenomenon → 1.00 €</li>
     *   <li>Rain phenomenon → 0.50 €</li>
     *   <li>No phenomenon / clear → 0.00 €</li>
     * </ul>
     *
     * @param vehicleType  the courier's vehicle type
     * @param observation  the most recent weather observation for the city
     * @return weather phenomenon extra fee in euros
     * @throws VehicleUsageForbiddenException if the phenomenon is glaze, hail, or thunder
     */
    public double calculatePhenomenonFee(VehicleType vehicleType, WeatherObservation observation) {
        if (!isWeatherSensitiveVehicle(vehicleType)) {
            return 0.0;
        }

        String phenomenon = observation.getWeatherPhenomenon();
        if (phenomenon == null || phenomenon.isBlank()) {
            return 0.0;
        }

        String normalized = phenomenon.toLowerCase();

        if (isForbiddenPhenomenon(normalized)) {
            throw new VehicleUsageForbiddenException("Usage of selected vehicle type is forbidden");
        }
        if (isSnowOrSleet(normalized)) {
            return SNOW_SLEET_PHENOMENON_FEE;
        }
        if (isRain(normalized)) {
            return RAIN_PHENOMENON_FEE;
        }
        return 0.0;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private boolean isWeatherSensitiveVehicle(VehicleType vehicleType) {
        return vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE;
    }

    private boolean isForbiddenPhenomenon(String phenomenon) {
        return phenomenon.contains("glaze")
                || phenomenon.contains("hail")
                || phenomenon.contains("thunder");
    }

    private boolean isSnowOrSleet(String phenomenon) {
        return phenomenon.contains("snow") || phenomenon.contains("sleet");
    }

    private boolean isRain(String phenomenon) {
        return phenomenon.contains("rain") || phenomenon.contains("shower");
    }
}
