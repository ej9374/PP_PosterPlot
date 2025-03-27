# PosterPlot
AI 기반 줄거리 생성과 유저 작성 시나리오를 바탕으로 시나리오 열람 기능을 제공하는 웹 플랫폼

<img src="https://user-images.githubusercontent.com/your-uploaded-image.png" width="600"/>


# PosterPlot Backend (Spring + Flask + MySQL)

이 프로젝트는 영화 포스터 기반 AI 줄거리 생성 시스템의 백엔드입니다.  
Spring Boot + Flask + MySQL 3가지 서비스를 **Docker Compose**를 통해 실행합니다.

---

## 서비스

| 서비스      | 설명                              | 포트          |
|-------------|-----------------------------------|---------------|
| backend     | Spring Boot 기반 REST API 서버     | `localhost:8080` |
| flask       | BLIP + Mistral 모델 기반 AI 생성기 | `localhost:5000` |
| db (MySQL)  | 포스터 및 유저 데이터 저장용 DB   | 컨테이너 내부: 3306<br>호스트: **3307** ← 로컬 충돌 방지용 |

---

## 실행 방법

### 1. Docker 설치

- [Docker Desktop 설치하기 (Windows/Mac)](https://www.docker.com/products/docker-desktop)

> 💡 설치 후, 터미널에서 아래 명령어로 버전 확인:
> ```bash
> docker --version
> docker compose version
> ```

---

### 2. 프로젝트 실행

```bash
# docker-compose.yml이 있는 루트 폴더로 이동

# 모든 컨테이너 빌드 및 실행
docker compose up --build
