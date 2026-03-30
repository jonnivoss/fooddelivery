package com.fooddelivery.deliveryfee.dto;

import com.fooddelivery.deliveryfee.entity.City;
import com.fooddelivery.deliveryfee.entity.VehicleType;
import jakarta.validation.constraints.NotNull;

/**
 * Request parameters for the delivery fee calculation endpoint.
 */
public record DeliveryFeeRequest(

        /**
         * The city where the delivery takes place. Must not be null.
         */
        @NotNull(message = "City must not be null")
        City city,

        /**
         * The type of vehicle used for the delivery. Must not be null.
         */
        @NotNull(message = "Vehicle type must not be null")
        VehicleType vehicleType
) {}
