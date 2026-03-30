package com.fooddelivery.deliveryfee.controller;

import com.fooddelivery.deliveryfee.dto.DeliveryFeeRequest;
import com.fooddelivery.deliveryfee.dto.DeliveryFeeResponse;
import com.fooddelivery.deliveryfee.entity.City;
import com.fooddelivery.deliveryfee.entity.VehicleType;
import com.fooddelivery.deliveryfee.service.DeliveryFeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the delivery fee calculation endpoint.
 *
 * <p>Base path: {@code /api/v1/delivery-fee}
 */
@RestController
@RequestMapping("/api/v1/delivery-fee")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Calculates the delivery fee for the given city and vehicle type.
     *
     * <p>Example request:
     * <pre>GET /api/v1/delivery-fee?city=TALLINN&amp;vehicleType=BIKE</pre>
     *
     * @param city        delivery city ({@code TALLINN}, {@code TARTU}, or {@code PARNU})
     * @param vehicleType courier vehicle ({@code CAR}, {@code SCOOTER}, or {@code BIKE})
     * @return 200 with a {@link DeliveryFeeResponse} fee breakdown,
     *         422 if weather conditions forbid the chosen vehicle,
     *         404 if no weather data is available for the city,
     *         400 if parameters are missing or invalid
     */
    @GetMapping
    public ResponseEntity<DeliveryFeeResponse> getDeliveryFee(
            @RequestParam @Valid City city,
            @RequestParam @Valid VehicleType vehicleType) {

        DeliveryFeeRequest request = new DeliveryFeeRequest(city, vehicleType);
        DeliveryFeeResponse response = deliveryFeeService.calculateFee(request);
        return ResponseEntity.ok(response);
    }
}
