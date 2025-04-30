FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} HisTour.jar

CMD ["java", "-jar", "HisTour.jar"]
