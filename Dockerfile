FROM amazoncorretto:11-alpine AS build

WORKDIR /work
COPY . /work

RUN chmod +x gradlew
RUN ./gradlew --no-daemon :shadowJar

FROM amazoncorretto:11-alpine

COPY --from=build /work/build/libs/market-*-all.jar /app/market.jar

ENTRYPOINT ["java","-jar","/app/market.jar"]
