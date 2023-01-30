# Use an openjdk image as the base image
FROM openjdk:11-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the jar file to the working directory
COPY target/your-spring-boot-app.jar .

# Set the entrypoint to run the jar file
ENTRYPOINT ["java", "-jar", "your-spring-boot-app.jar"]
