#
# Build stage
#
FROM maven:3.9.4-eclipse-temurin-20 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install -Dmaven.test.skip
#
# Package stage
#
FROM openjdk:20-jdk-slim
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

ENV DEFAULT_BUSINESS_ID=2