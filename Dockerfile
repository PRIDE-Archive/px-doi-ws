# Build stage
FROM maven:3.3.9-jdk-8-alpine AS build-env

# Create app directory
WORKDIR /app

COPY src ./src
COPY pom.xml ./
COPY config/application.yml ./application.yml
RUN mvn clean package -DskipTests

# Package stage
FROM maven:3.3.9-jdk-8-alpine
WORKDIR /app
COPY --from=build-env /app/target/px-doi-api.jar ./
ENTRYPOINT java ${JAVA_OPTS} -jar px-doi-api.jar