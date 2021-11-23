FROM openjdk:11 AS build

ARG GH_USER
ARG GH_TOKEN

RUN mkdir /src
COPY .github/workflows /src
WORKDIR /src
RUN ./gradlew fulljar --no-daemon

FROM adoptopenjdk/openjdk11:alpine

RUN mkdir /app
COPY --from=build /src/build/libs/*.jar /app/application.jar

ARG PROJECT_NAME
ARG PROJECT_VERSION

ENV APPLICATION_NAME=$PROJECT_NAME
ENV APPLICATION_VERSION=$PROJECT_VERSION

CMD ["java", "-jar", "/app/application.jar"]