package com.fooddelivery.deliveryfee.entity;

/**
 * Supported delivery cities, each mapped to the corresponding weather station name
 * used in Estonian Environment Agency data feeds.
 */
public enum City {

    TALLINN("Tallinn-Harku"),
    TARTU("Tartu-Tõravere"),
    PARNU("Pärnu");

    private final String stationName;

    City(String stationName) {
        this.stationName = stationName;
    }

    /**
     * Returns the weather station name used to look up observations for this city.
     *
     * @return weather station name
     */
    public String getStationName() {
        return stationName;
    }
}
