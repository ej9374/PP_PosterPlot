# 1단계: 경량 JDK 이미지 사용
FROM openjdk:17-jdk-slim

# 2단계: 작업 디렉토리 생성
WORKDIR /app

# 3단계: jar 파일 복사 (빌드 후)
COPY build/libs/*.jar app.jar

# 4단계: 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 필요 시 포트 노출
EXPOSE 8080
