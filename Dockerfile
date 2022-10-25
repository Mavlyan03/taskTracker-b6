FROM maven:3.8.6-jdk-11-slim AS build

WORKDIR /b6/build

COPY . /b6/build

RUN mvn clean install -DskiptTests=true

FROM openjdk:11.0.11-jre-slim
WORKDIR /b6/app

COPY --from=build /b6/build/target/tasktracker-b6-0.0.1-SNAPSHOT.jar /b6/app/

EXPOSE 80

ENTRYPOINT ["java","-jar","/b6/app/tasktracker-b6-0.0.1-SNAPSHOT.jar"]