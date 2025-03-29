# 1단계: 경량 JDK 이미지 사용
FROM openjdk:17-jdk-slim

# 2단계: 작업 디렉토리 생성
WORKDIR /app

# 3단계: jar 파일 복사
COPY build/libs/*.jar app.jar

# wait-for-it.sh 복사
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# 4단계: 애플리케이션 실행 전에 Flask(5000)와 DB(3306)가 켜질 때까지 대기
ENTRYPOINT ["/wait-for-it.sh", "flask:5000", "--", "/wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar"]

# 포트 노출
EXPOSE 8080
