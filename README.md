# Food Delivery Fee Calculator

A Spring Boot REST API that calculates dynamic delivery fees based on regional location, vehicle type, and real-time weather conditions using data from the Estonian Environment Agency.

## 🎯 Project Overview

This application provides a sophisticated delivery fee calculation system that takes into account:
- **Regional base fees** varying by delivery city
- **Vehicle-specific restrictions** (cars, scooters, bikes)
- **Dynamic weather adjustments** including temperature, wind speed, and weather phenomena
- **Real-time weather data** automatically imported from official sources

### Key Features

✅ **RESTful API** - Simple HTTP GET endpoint for fee calculation  
✅ **Real-time Weather Integration** - Automatic weather data import from Estonian Environment Agency  
✅ **Scheduled Updates** - Periodic weather data refresh (configurable cron schedule)  
✅ **Dynamic Fee Calculation** - Base fare + weather-based surcharges  
✅ **Vehicle Restrictions** - Certain weather conditions forbid certain vehicle types (e.g., bikes in storms)  
✅ **H2 Database** - Embedded database for development, PostgreSQL-ready for production  
✅ **Comprehensive Error Handling** - Clear error messages for missing data or vehicle restrictions  

## 📋 Requirements Met

- [x] Delivery fee calculation based on city and vehicle type
- [x] Weather data storage table with: station name, WMO code, air temperature, wind speed, phenomenon, timestamp
- [x] Regional base fees for three cities: Tallinn, Tartu, Pärnu
- [x] Three vehicle types supported: Car, Scooter, Bike
- [x] Weather-based extra fees for temperature, wind, and phenomena
- [x] Vehicle usage restrictions based on weather

## 🏗️ Architecture

### Technology Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.5.13 |
| **Language** | Java 21 |
| **Database** | H2 (dev) / PostgreSQL (prod) |
| **Build Tool** | Gradle 8.5+ |
| **Persistence** | Spring Data JPA + Hibernate 6.6 |
| **Weather Data** | XML feed from ilmateenistus.ee |

### Project Structure

```
deliveryfee/
├── src/main/java/com/fooddelivery/deliveryfee/
│   ├── DeliveryfeeApplication.java          # Main application entry point
│   ├── controller/
│   │   └── DeliveryFeeController.java       # REST endpoint
│   ├── service/
│   │   ├── DeliveryFeeService.java          # Orchestrates fee calculation
│   │   ├── WeatherImportService.java        # Fetches & parses weather data
│   │   ├── RegionalBaseFeeCalculator.java   # City & vehicle base fees
│   │   └── WeatherExtraFeeCalculator.java   # Weather surcharge logic
│   ├── entity/
│   │   ├── WeatherObservation.java          # JPA entity for weather table
│   │   ├── City.java                        # Enum: TALLINN, TARTU, PARNU
│   │   └── VehicleType.java                 # Enum: CAR, SCOOTER, BIKE
│   ├── repository/
│   │   └── WeatherObservationRepository.java # Data access layer
│   ├── dto/
│   │   ├── DeliveryFeeRequest.java          # API request DTO
│   │   ├── DeliveryFeeResponse.java         # API response DTO
│   │   └── ErrorResponse.java               # Error response format
│   ├── exception/
│   │   ├── WeatherDataNotFoundException.java
│   │   └── VehicleUsageForbiddenException.java
│   ├── config/
│   │   └── GlobalExceptionHandler.java      # Centralized error handling
│   └── scheduler/
│       └── WeatherImportScheduler.java      # Scheduled weather imports
├── src/main/resources/
│   ├── application.properties                # Default configuration
│   └── application-prod.properties           # Production configuration
└── build.gradle                              # Gradle build configuration
```

## 🚀 Getting Started

### Prerequisites

- **Java 21+** - [Eclipse Temurin JDK 21](https://adoptium.net/)
- **Gradle 8.5+** - Included via gradlew
- **PostgreSQL 12+** (optional, for production)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd deliveryfee
   ```

2. **Build the project**
   ```bash
   ./gradlew clean build
   ```

3. **Run the application**
   ```bash
   java -jar build/libs/deliveryfee-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application**
   - API: `http://localhost:8080/api/v1/delivery-fee`
   - H2 Console: `http://localhost:8080/h2-console`

### H2 Console Access

- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User:** `sa`
- **Password:** (leave empty)

Query the weather data:
```sql
SELECT * FROM WEATHER_OBSERVATION ORDER BY OBSERVED_AT DESC LIMIT 10;
```

## 📡 API Documentation

### Calculate Delivery Fee

**Endpoint:** `GET /api/v1/delivery-fee`

**Query Parameters:**

| Parameter | Type | Values | Example |
|-----------|------|--------|---------|
| `city` | String | TALLINN, TARTU, PARNU | `TALLINN` |
| `vehicleType` | String | CAR, SCOOTER, BIKE | `BIKE` |

**Request Example:**
```bash
curl "http://localhost:8080/api/v1/delivery-fee?city=TALLINN&vehicleType=BIKE"
```

**Success Response (200 OK):**
```json
{
  "city": "TALLINN",
  "vehicleType": "BIKE",
  "baseFee": 4.00,
  "temperatureFee": 1.00,
  "windSpeedFee": 0.50,
  "weatherPhenomenonFee": 0.00,
  "totalFee": 5.50
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-03-30T20:06:50.618Z",
  "status": 404,
  "error": "Not Found",
  "message": "No weather data available for city: TALLINN"
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "timestamp": "2026-03-30T20:10:20.000Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Usage of selected vehicle type is forbidden"
}
```

## 💰 Fee Calculation Logic

### Base Fees (€)

| City | Car | Scooter | Bike |
|------|-----|---------|------|
| Tallinn | 4.00 | 3.50 | 3.00 |
| Tartu | 3.50 | 3.00 | 2.50 |
| Pärnu | 3.00 | 2.50 | 2.00 |

### Weather Surcharges

#### Air Temperature Extra Fee (ATEF)
- **Applies to:** Scooter and Bike only
- **Temperature < -10°C:** +1.00€
- **-10°C ≤ Temperature ≤ 0°C:** +0.50€
- **Temperature > 0°C:** No surcharge

#### Wind Speed Extra Fee (WSEF)
- **Applies to:** Bike only
- **10 m/s ≤ Wind Speed ≤ 20 m/s:** +0.50€
- **Wind Speed > 20 m/s:** ❌ **Usage of selected vehicle type is forbidden**
- **Wind Speed < 10 m/s:** No surcharge

#### Weather Phenomenon Extra Fee (WPEF)
- **Applies to:** Scooter and Bike only
- **Snow or Sleet:** +1.00€
- **Rain:** +0.50€
- **Glaze, Hail, or Thunder:** ❌ **Usage of selected vehicle type is forbidden**
- **Clear/Other:** No surcharge

### Vehicle Restrictions

- **Bike:** Forbidden if wind speed ≥ 20 m/s
- **Scooter:** Forbidden if wind speed ≥ 20 m/s or thunderstorm active
- **Car:** No restrictions

## 📊 Database Schema

### weather_observation Table

```sql
CREATE TABLE weather_observation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  station_name VARCHAR(255) NOT NULL,
  wmo_code VARCHAR(10),
  air_temperature DOUBLE,
  wind_speed DOUBLE,
  weather_phenomenon VARCHAR(255),
  observed_at TIMESTAMP NOT NULL,
  
  INDEX idx_station_name (station_name),
  INDEX idx_observed_at (observed_at)
);
```

**Supported Stations:**
- `Tallinn-Harku` (Tallinn)
- `Tartu-Tõravere` (Tartu)
- `Pärnu` (Pärnu)

## ⚙️ Configuration

### Development (Default)

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
weather.import.cron=0 * * * * *        # Every minute
spring.h2.console.enabled=true
```

### Production

Set environment variables:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/deliveryfee
export DB_USERNAME=user
export DB_PASSWORD=password
export DDL_AUTO=validate
export WEATHER_CRON=0 15 * * * *       # Every hour at HH:15
export H2_CONSOLE_ENABLED=false
```

Run with production profile:
```bash
java -jar deliveryfee-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 📅 Weather Data Updates

Weather data is automatically imported from the Estonian Environment Agency:
- **Source:** `https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php`
- **Schedule:** Configurable via `weather.import.cron` property
- **Default Dev:** Every minute (`0 * * * * *`)
- **Default Prod:** Every hour at 15 minutes past (`0 15 * * * *`)
- **Startup:** Initialization on application startup via `@PostConstruct`

## 🧪 Testing

Run the test suite:
```bash
./gradlew test
```

### Test Report
After running tests, view the report at:
```
build/reports/tests/test/index.html
```

## 🐛 Troubleshooting

### "No weather data available for city: TALLINN"
- Wait for the scheduled weather import to complete (check logs)
- Ensure network connectivity to `ilmateenistus.ee`
- Check that the application has been running for at least 1 minute

### "BIKE usage is forbidden in current weather conditions"
- The wind speed is too high or extreme weather (thunderstorm, hail) is present
- Try with `CAR` or `SCOOTER` vehicle type instead

### Port Already in Use
Set a different port:
```bash
java -jar deliveryfee-0.0.1-SNAPSHOT.jar --server.port=9090
```

## 📝 Logging

View application logs for debugging:

```bash
# Development - verbose logging
logging.level.com.fooddelivery.deliveryfee=INFO

# Production - concise logging
logging.level.com.fooddelivery.deliveryfee=WARN
```

Check for successful weather imports:
```
Weather import complete — 3 observation(s) saved
```

## 📦 Deployment

### Docker
```bash
docker build -t deliveryfee:latest ./deliveryfee
docker run -e DB_URL="jdbc:postgresql://postgres:5432/deliveryfee" \
  -e DB_USERNAME="user" \
  -e DB_PASSWORD="password" \
  -p 8080:8080 deliveryfee:latest
```

### Production JAR
```bash
./gradlew clean build
java -jar build/libs/deliveryfee-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/deliveryfee \
  --spring.datasource.username=user \
  --spring.datasource.password=password
```

## 📄 License

This project is submitted for the [Competition Name]. All rights reserved.

## ✉️ Contact

For questions or issues, contact the development team.

---

**Last Updated:** March 30, 2026  
**Version:** 0.0.1-SNAPSHOT  
**Java Version:** 21  
**Spring Boot:** 3.5.13
