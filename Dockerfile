FROM amazoncorretto:11-alpine-jdk

COPY target/schoolManagment-0.0.1-SNAPSHOT.jar /api-eduapp-v1.jar

ENTRYPOINT ["java", "-jar", "/api-eduapp-v1.jar"]