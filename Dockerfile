# Use an openjdk image as the base image
FROM openjdk:11-jdk-alpine

# Set the working directory
WORKDIR /

# Copy the jar file to the working directory
COPY target/thullo-0.0.1-SNAPSHOT.jar thullo.jar

# Set the entrypoint to run the jar file
ENTRYPOINT ["java", "-jar", "/thullo.jar"]
