package com.fooddelivery.deliveryfee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fooddelivery.deliveryfee.entity.City;
import com.fooddelivery.deliveryfee.entity.VehicleType;

/**
 * Response payload containing the calculated delivery fee and its breakdown.
 *
 * @param city            the city the fee was calculated for
 * @param vehicleType     the vehicle type used in the calculation
 * @param regionalBaseFee base fee determined by city and vehicle type (€)
 * @param temperatureFee  extra fee for low air temperature (€), 0 if not applicable
 * @param windSpeedFee    extra fee for high wind speed (€), 0 if not applicable
 * @param phenomenonFee   extra fee for adverse weather phenomena (€), 0 if not applicable
 * @param totalFee        sum of all applicable fees (€)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeliveryFeeResponse(
        City city,
        VehicleType vehicleType,
        double regionalBaseFee,
        double temperatureFee,
        double windSpeedFee,
        double phenomenonFee,
        double totalFee
) {

    /**
     * Constructs a response by summing all fee components.
     */
    public static DeliveryFeeResponse of(City city,
                                          VehicleType vehicleType,
                                          double regionalBaseFee,
                                          double temperatureFee,
                                          double windSpeedFee,
                                          double phenomenonFee) {
        double total = regionalBaseFee + temperatureFee + windSpeedFee + phenomenonFee;
        return new DeliveryFeeResponse(city, vehicleType,
                regionalBaseFee, temperatureFee, windSpeedFee, phenomenonFee, total);
    }
}
