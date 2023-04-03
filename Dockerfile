FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:11
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/
ENTRYPOINT ["java","-jar","/app/fat_server.jar"]