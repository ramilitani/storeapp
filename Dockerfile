#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine
EXPOSE 8080
COPY --from=build /home/app/target/store-app-1.0.0-SNAPSHOT.jar /usr/local/lib/storeapp.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/storeapp.jar"]