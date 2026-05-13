# Step 1: Use Eclipse Temurin for Maven build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application using standard mvn (more reliable if mvnw is missing)
RUN mvn clean package -DskipTests

# Step 2: Use a stable and slim Runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/food-ordering-system-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]