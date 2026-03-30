package com.fooddelivery.deliveryfee.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Represents a single weather observation imported from the Estonian Environment Agency.
 *
 * <p>Each row is an immutable snapshot — historical records are never overwritten.
 * New records are appended on every scheduled import run.
 */
@Entity
@Table(name = "weather_observation", indexes = {
        @Index(name = "idx_station_name", columnList = "station_name"),
        @Index(name = "idx_observed_at", columnList = "observed_at")
})
public class WeatherObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable name of the weather station (e.g. "Tallinn-Harku").
     */
    @Column(name = "station_name", nullable = false)
    private String stationName;

    /**
     * WMO (World Meteorological Organisation) code identifying the station.
     */
    @Column(name = "wmo_code")
    private String wmoCode;

    /**
     * Measured air temperature in degrees Celsius.
     */
    @Column(name = "air_temperature")
    private Double airTemperature;

    /**
     * Measured wind speed in metres per second.
     */
    @Column(name = "wind_speed")
    private Double windSpeed;

    /**
     * Textual description of the current weather phenomenon (e.g. "Light snow", "Rain").
     * May be null or empty when conditions are clear.
     */
    @Column(name = "weather_phenomenon")
    private String weatherPhenomenon;

    /**
     * UTC timestamp of the observation as reported by the weather station.
     */
    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;

    protected WeatherObservation() {
    }

    public WeatherObservation(String stationName,
                               String wmoCode,
                               Double airTemperature,
                               Double windSpeed,
                               String weatherPhenomenon,
                               Instant observedAt) {
        this.stationName = stationName;
        this.wmoCode = wmoCode;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
        this.observedAt = observedAt;
    }

    public Long getId() { return id; }
    public String getStationName() { return stationName; }
    public String getWmoCode() { return wmoCode; }
    public Double getAirTemperature() { return airTemperature; }
    public Double getWindSpeed() { return windSpeed; }
    public String getWeatherPhenomenon() { return weatherPhenomenon; }
    public Instant getObservedAt() { return observedAt; }

    @Override
    public String toString() {
        return "WeatherObservation{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                ", weatherPhenomenon='" + weatherPhenomenon + '\'' +
                ", observedAt=" + observedAt +
                '}';
    }
}
