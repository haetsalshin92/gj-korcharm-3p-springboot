# 1. Base Image: Java 17 JDK (Amazon Corretto or OpenJDK)
FROM openjdk:17-jdk-slim

# 2. 환경 변수
ENV TZ=Asia/Seoul

# 3. JAR 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 실행 포트
EXPOSE 8080

# 5. 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]