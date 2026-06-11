FROM eclipse-temurin:21-jdk as builder
LABEL authors="Koder95"
WORKDIR school-booking-platform-backend
COPY src/ ./src
COPY mvnw ./
COPY .mvn/ ./.mvn/
COPY pom.xml ./
COPY checkstyle.xml ./
RUN ./mvnw clean package -DskipTests
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} sbpb.jar
RUN java -Djarmode=layertools -jar sbpb.jar extract

FROM eclipse-temurin:21-jre-alpine
WORKDIR school-booking-platform-backend
COPY --from=builder school-booking-platform-backend/dependencies/ ./
COPY --from=builder school-booking-platform-backend/spring-boot-loader/ ./
COPY --from=builder school-booking-platform-backend/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
EXPOSE 54342
