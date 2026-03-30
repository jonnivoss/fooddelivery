package com.fooddelivery.deliveryfee.repository;

import com.fooddelivery.deliveryfee.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access layer for {@link WeatherObservation} records.
 */
@Repository
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {

    /**
     * Returns the most recent observation for the given weather station.
     *
     * <p>Used by the fee calculator to obtain current weather conditions for a city.
     *
     * @param stationName exact station name as stored in the database (e.g. "Tallinn-Harku")
     * @return the latest observation, or empty if no data has been imported yet
     */
    @Query("SELECT w FROM WeatherObservation w " +
           "WHERE w.stationName = :stationName " +
           "ORDER BY w.observedAt DESC " +
           "LIMIT 1")
    Optional<WeatherObservation> findLatestByStationName(@Param("stationName") String stationName);
}
