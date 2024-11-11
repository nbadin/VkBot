FROM openjdk:21-jdk
WORKDIR /app
COPY build/libs/VkBot-0.0.1-SNAPSHOT.jar /app/vkbot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "vkbot.jar"]
