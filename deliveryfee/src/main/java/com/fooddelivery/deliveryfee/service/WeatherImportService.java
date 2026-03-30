package com.fooddelivery.deliveryfee.service;

import com.fooddelivery.deliveryfee.entity.WeatherObservation;
import com.fooddelivery.deliveryfee.repository.WeatherObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fetches and persists weather observations from the Estonian Environment Agency XML feed.
 *
 * <p>Only observations for the three supported stations are stored;
 * all other stations in the feed are silently ignored.
 * Each import run inserts new rows — existing rows are never modified.
 */
@Service
public class WeatherImportService {

    private static final Logger log = LoggerFactory.getLogger(WeatherImportService.class);

    /** Stations of interest — must match names in the XML feed exactly. */
    private static final Set<String> TARGET_STATIONS = Set.of(
            "Tallinn-Harku",
            "Tartu-Tõravere",
            "Pärnu"
    );

    private final WeatherObservationRepository repository;
    private final String weatherUrl = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    public WeatherImportService(WeatherObservationRepository repository) {
        this.repository = repository;
    }

    /**
     * Downloads the latest weather observations and saves them to the database.
     *
     * <p>Called by the scheduled cron job in
     * {@link com.fooddelivery.deliveryfee.scheduler.WeatherImportScheduler}.
     * Can also be called manually for testing purposes.
     *
     * @return the number of new observation records saved
     */
    public int importWeatherData() {
        log.info("Starting weather data import from {}", weatherUrl);

        try {
            Document xml = fetchXml();
            List<WeatherObservation> observations = parseObservations(xml);
            repository.saveAll(observations);

            log.info("Weather import complete — {} observation(s) saved", observations.size());
            return observations.size();

        } catch (Exception e) {
            log.error("Weather data import failed: {}", e.getMessage(), e);
            return 0;
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Document fetchXml() throws Exception {
        try (InputStream stream = URI.create(weatherUrl).toURL().openStream()) {
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(stream);
        }
    }

    private List<WeatherObservation> parseObservations(Document doc) {
        Instant timestamp = parseTimestamp(doc);
        NodeList stations = doc.getElementsByTagName("station");
        List<WeatherObservation> result = new ArrayList<>();

        for (int i = 0; i < stations.getLength(); i++) {
            Element station = (Element) stations.item(i);
            String name = getText(station, "name");

            if (!TARGET_STATIONS.contains(name)) {
                continue;
            }

            WeatherObservation observation = new WeatherObservation(
                    name,
                    getText(station, "wmocode"),
                    parseDouble(station, "airtemperature"),
                    parseDouble(station, "windspeed"),
                    getText(station, "phenomenon"),
                    timestamp
            );

            result.add(observation);
            log.debug("Parsed observation: {}", observation);
        }

        return result;
    }

    private Instant parseTimestamp(Document doc) {
        try {
            String epochSeconds = doc.getDocumentElement().getAttribute("timestamp");
            return Instant.ofEpochSecond(Long.parseLong(epochSeconds));
        } catch (Exception e) {
            log.warn("Could not parse timestamp from XML, using current time");
            return Instant.now();
        }
    }

    private String getText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        String text = nodes.item(0).getTextContent();
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private Double parseDouble(Element parent, String tagName) {
        String text = getText(parent, tagName);
        if (text == null) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            log.warn("Could not parse numeric value for tag '{}': '{}'", tagName, text);
            return null;
        }
    }
}
