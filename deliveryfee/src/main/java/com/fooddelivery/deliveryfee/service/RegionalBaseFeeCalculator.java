package com.fooddelivery.deliveryfee.service;

import com.fooddelivery.deliveryfee.entity.City;
import com.fooddelivery.deliveryfee.entity.VehicleType;
import org.springframework.stereotype.Service;

/**
 * Calculates the Regional Base Fee (RBF) for a given city and vehicle type combination.
 *
 * <p>Fee table:
 * <pre>
 *              Car    Scooter   Bike
 * Tallinn      4.00   3.50      3.00
 * Tartu        3.50   3.00      2.50
 * Pärnu        3.00   2.50      2.00
 * </pre>
 */
@Service
public class RegionalBaseFeeCalculator {

    /**
     * Returns the base delivery fee in euros for the given city and vehicle type.
     *
     * @param city        the delivery city
     * @param vehicleType the courier's vehicle type
     * @return base fee in euros
     * @throws IllegalArgumentException if the city/vehicle combination is not configured
     */
    public double calculate(City city, VehicleType vehicleType) {
        return switch (city) {
            case TALLINN -> tallinnFee(vehicleType);
            case TARTU   -> tartuFee(vehicleType);
            case PARNU   -> parnuFee(vehicleType);
        };
    }

    private double tallinnFee(VehicleType vehicleType) {
        return switch (vehicleType) {
            case CAR     -> 4.00;
            case SCOOTER -> 3.50;
            case BIKE    -> 3.00;
        };
    }

    private double tartuFee(VehicleType vehicleType) {
        return switch (vehicleType) {
            case CAR     -> 3.50;
            case SCOOTER -> 3.00;
            case BIKE    -> 2.50;
        };
    }

    private double parnuFee(VehicleType vehicleType) {
        return switch (vehicleType) {
            case CAR     -> 3.00;
            case SCOOTER -> 2.50;
            case BIKE    -> 2.00;
        };
    }
}
