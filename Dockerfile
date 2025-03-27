# Python 3.12.4 기반 이미지 사용
FROM python:3.12.4-slim

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 파일 복사
COPY . .

# pip 최신화 및 의존성 설치
RUN pip install --upgrade pip \
    && pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu \
    && pip install transformers pillow requests flask flask-cors mtranslate

# Flask 서버 외부에서 접근 가능하도록 포트 열기
EXPOSE 5000

# 앱 실행
CMD ["python", "posterplot_ai.py"]
