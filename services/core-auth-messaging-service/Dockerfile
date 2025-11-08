FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Expect the application jar to be built with `mvn package` prior to docker build
COPY target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
