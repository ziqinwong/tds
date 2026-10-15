# Car Management Service

This service helps to manage a car park of 100 spaces. Users can check how full a car park is, park vehicles, and calculate parking charges when exiting the car park. Built using Java and Spring Boot.

## Building and running
You will need the following to run this application:
- JDK 21
- Gradle 9

1. Clone the repository
2. Run .\gradlew.bat bootRun
3. The application will start on port 8080

## Assumptions
1. The size of the car park is set at 100 spaces. If this needed to be configurable this value could be passed through as a property instead of hard coded.
2. VehicleRegs are unique per car, duplicate parking requests are rejected.
3. Time in and Time out are stored as Instant timestamps (UTC timezone).
4. Charges are only calculated for full minutes, partial minutes are just rounded down.
