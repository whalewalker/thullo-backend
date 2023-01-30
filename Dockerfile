FROM amazoncorretto:17.0.5-alpine3.16
MAINTAINER "Abel Michael"
WORKDIR /
ADD thullo*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
