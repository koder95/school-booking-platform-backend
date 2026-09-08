FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine as builder
LABEL authors="Koder95"
WORKDIR school-booking-platform-backend
ARG SBP_BACKEND_VERSION=1.0-SNAPSHOT
COPY src/ ./src
COPY pom.xml ./
COPY checkstyle.xml ./
RUN mvn clean package -DskipTests -Drevision=${SBP_BACKEND_VERSION}
ARG JAR_FILE=target/*.jar
RUN cp ${JAR_FILE} sbpb.jar
RUN java -Djarmode=tools -jar sbpb.jar extract --layers --launcher

FROM ubuntu/jre:21-24.04_stable
WORKDIR school-booking-platform-backend
COPY --from=builder school-booking-platform-backend/sbpb/dependencies/ ./
COPY --from=builder school-booking-platform-backend/sbpb/spring-boot-loader/ ./
COPY --from=builder school-booking-platform-backend/sbpb/application/ ./
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:54342", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
EXPOSE 54342
