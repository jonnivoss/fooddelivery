package com.fooddelivery.deliveryfee.service;

import com.fooddelivery.deliveryfee.dto.DeliveryFeeRequest;
import com.fooddelivery.deliveryfee.dto.DeliveryFeeResponse;
import com.fooddelivery.deliveryfee.entity.City;
import com.fooddelivery.deliveryfee.entity.VehicleType;
import com.fooddelivery.deliveryfee.entity.WeatherObservation;
import com.fooddelivery.deliveryfee.exception.WeatherDataNotFoundException;
import com.fooddelivery.deliveryfee.repository.WeatherObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates delivery fee calculation by combining:
 * <ol>
 *   <li>Regional Base Fee from {@link RegionalBaseFeeCalculator}</li>
 *   <li>Weather-related extra fees from {@link WeatherExtraFeeCalculator}</li>
 * </ol>
 *
 * <p>The most recent weather observation for the requested city is fetched
 * from the database before any fee computation begins.
 */
@Service
public class DeliveryFeeService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryFeeService.class);

    private final WeatherObservationRepository weatherRepository;
    private final RegionalBaseFeeCalculator baseFeeCalculator;
    private final WeatherExtraFeeCalculator extraFeeCalculator;

    public DeliveryFeeService(WeatherObservationRepository weatherRepository,
                               RegionalBaseFeeCalculator baseFeeCalculator,
                               WeatherExtraFeeCalculator extraFeeCalculator) {
        this.weatherRepository = weatherRepository;
        this.baseFeeCalculator = baseFeeCalculator;
        this.extraFeeCalculator = extraFeeCalculator;
    }

    /**
     * Calculates the total delivery fee for the given city and vehicle type.
     *
     * <p>Steps:
     * <ol>
     *   <li>Retrieve the latest weather observation for the city's station.</li>
     *   <li>Compute the regional base fee.</li>
     *   <li>Compute each weather extra fee component (may throw if conditions forbid the vehicle).</li>
     *   <li>Return a detailed fee breakdown.</li>
     * </ol>
     *
     * @param request contains the target city and vehicle type
     * @return a full breakdown of the calculated delivery fee
     * @throws WeatherDataNotFoundException   if no weather data exists for the city
     * @throws com.fooddelivery.deliveryfee.exception.VehicleUsageForbiddenException
     *         if current weather conditions prohibit the chosen vehicle type
     */
    public DeliveryFeeResponse calculateFee(DeliveryFeeRequest request) {
        City city = request.city();
        VehicleType vehicleType = request.vehicleType();

        log.info("Calculating delivery fee for city={}, vehicleType={}", city, vehicleType);

        WeatherObservation observation = fetchLatestObservation(city);

        double baseFee        = baseFeeCalculator.calculate(city, vehicleType);
        double temperatureFee = extraFeeCalculator.calculateTemperatureFee(vehicleType, observation);
        double windSpeedFee   = extraFeeCalculator.calculateWindSpeedFee(vehicleType, observation);
        double phenomenonFee  = extraFeeCalculator.calculatePhenomenonFee(vehicleType, observation);

        DeliveryFeeResponse response = DeliveryFeeResponse.of(
                city, vehicleType, baseFee, temperatureFee, windSpeedFee, phenomenonFee);

        log.info("Fee calculation result: totalFee={} for city={}, vehicleType={}",
                response.totalFee(), city, vehicleType);

        return response;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private WeatherObservation fetchLatestObservation(City city) {
        String stationName = city.getStationName();
        return weatherRepository.findLatestByStationName(stationName)
                .orElseThrow(() -> new WeatherDataNotFoundException(city.name()));
    }
}
