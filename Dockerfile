FROM openjdk:8-jdk-alpine

COPY target/propbuddy-0.0.1-SNAPSHOT.jar propbuddy.jar

EXPOSE 8081

CMD ["java", "-jar", "/propbuddy.jar"]