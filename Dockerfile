# Step 1: Use Eclipse Temurin for Maven build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application using standard mvn, skipping tests
RUN mvn clean package -Dmaven.test.skip=true

# Step 2: Use a stable and slim Runtime image
FROM eclipse-temurin:17-jre

# --- TEMPORARY CONNECTION TEST ---
# Switch to root user to install packages
USER root

# Install mysql-client and test connection
RUN apt-get update && apt-get install -y default-mysql-client && \
    echo "--- Attempting to connect to the database... ---" && \
    mysql -h yamabiko.proxy.rlwy.net -P 14395 -u root -p'TscphnAMmUCynfRrvWjlETlQjOboNeSu' -e "SELECT 1;" && \
    echo "--- Database connection test successful! ---"

# Switch back to the default user
USER 1000
# --- END OF TEMPORARY TEST ---

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/food-ordering-system-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
