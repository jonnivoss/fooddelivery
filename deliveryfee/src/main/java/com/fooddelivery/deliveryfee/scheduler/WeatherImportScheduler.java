package com.fooddelivery.deliveryfee.scheduler;

import com.fooddelivery.deliveryfee.service.WeatherImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task that triggers periodic weather data imports.
 *
 * <p>The cron expression is fully configurable via the
 * {@code weather.import.cron} property in {@code application.properties}.
 *
 * <p>Default schedule: every hour at HH:15:00
 * (cron expression: {@code 0 15 * * * *}).
 *
 * <p>Example overrides in {@code application.properties}:
 * <pre>
 * # Every minute (useful for local development)
 * weather.import.cron=0 * * * * *
 *
 * # Every day at 06:15
 * weather.import.cron=0 15 6 * * *
 * </pre>
 */
@Component
public class WeatherImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherImportScheduler.class);

    private final WeatherImportService weatherImportService;

    public WeatherImportScheduler(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    /**
     * Executes the weather import job on the configured schedule.
     *
     * <p>The cron expression is read from {@code weather.import.cron}.
     * Failures are logged by {@link WeatherImportService} and do not propagate
     * as exceptions, so the scheduler will continue running on subsequent ticks.
     */
    @Scheduled(cron = "${weather.import.cron}")
    public void runWeatherImport() {
        log.info("Scheduled weather import triggered");
        weatherImportService.importWeatherData();
    }
}
