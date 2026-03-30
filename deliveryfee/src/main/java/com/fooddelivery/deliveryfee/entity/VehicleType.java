package com.fooddelivery.deliveryfee.entity;

/**
 * Vehicle types available for food delivery couriers.
 */
public enum VehicleType {

    /** Four-wheeled motor vehicle. Not affected by weather extra fees. */
    CAR,

    /** Two-wheeled motor scooter. Affected by temperature and weather phenomenon fees. */
    SCOOTER,

    /** Pedal bicycle. Affected by all weather-related extra fees. */
    BIKE
}
