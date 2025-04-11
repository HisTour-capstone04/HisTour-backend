FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/HisTour-0.0.1-SNAPSHOT.jar HisTour.jar

CMD ["java", "-jar", "HisTour.jar"]
