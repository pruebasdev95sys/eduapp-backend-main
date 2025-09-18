# Stage 1: Build con Maven
FROM maven:3.8.4-openjdk-11 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Stage 2: Runtime con fuentes
FROM amazoncorretto:11-alpine-jdk

# Instalar fuentes Arial en Alpine
RUN apk update && apk add --no-cache \
    fontconfig \
    ttf-dejavu \
    msttcorefonts-installer \
    && update-ms-fonts \
    && fc-cache -f

# Copiar el JAR desde la stage de build
COPY --from=build /app/target/schoolManagment-0.0.1-SNAPSHOT.jar /api-eduapp-v1.jar

ENTRYPOINT ["java", "-jar", "/api-eduapp-v1.jar"]