FROM amazoncorretto:11-alpine-jdk

# Instalar fuentes Arial en Alpine + Maven para construir
RUN apk update && apk add --no-cache \
    fontconfig \
    ttf-dejavu \
    msttcorefonts-installer \
    maven \
    && update-ms-fonts \
    && fc-cache -f

# Copiar el código fuente
COPY . /app
WORKDIR /app

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Copiar el JAR generado
COPY target/schoolManagment-0.0.1-SNAPSHOT.jar /api-eduapp-v1.jar

ENTRYPOINT ["java", "-jar", "/api-eduapp-v1.jar"]