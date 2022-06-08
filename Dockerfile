FROM openjdk:8-jdk-alpine
USER root
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /var/app/
RUN chown -R spring:spring /var/app/
RUN chmod -R 777 /var/app/
USER spring:spring
ARG JAR_FILE=tempo-cc-api-app/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]