FROM openjdk:11
ADD target/set-car-game-server.jar set-car-game-server.jar
ENTRYPOINT ["java","-jar","/set-car-game-server.jar"]