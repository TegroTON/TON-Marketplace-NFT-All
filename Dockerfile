FROM amazoncorretto:17-alpine AS build

WORKDIR /work
COPY . /work

RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM amazoncorretto:17-alpine

COPY --from=build /work/build/libs/market-*.jar /app/market.jar

ENTRYPOINT ["java","-jar","/app/market.jar"]
