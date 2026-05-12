# Use Maven image for building
FROM maven:3.9-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Use smaller runtime image
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/food-ordering-system-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
