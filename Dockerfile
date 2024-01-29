FROM openjdk:17
ADD target/asset.jar asset.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "asset.jar"]