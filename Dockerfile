# Use a lightweight JDK base image
FROM eclipse-temurin:17-jdk

# Set work directory
WORKDIR /app

# Copy the built jar (update the jar name if needed)
COPY target/kafkaplayground-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]