FROM openjdk:17
ADD target/asset.jar asset.jar
ENTRYPOINT ["java", "-jar", "asset.jar"]