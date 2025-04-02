from flask import Flask, request, jsonify
from dotenv import load_dotenv
import torch
from transformers import BlipProcessor, BlipForConditionalGeneration
from PIL import Image
import requests
from mtranslate import translate
import re
from flask_cors import CORS
import time
import os

load_dotenv()  # .env 파일 자동 로드

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})

# 모델 로드
device = "cuda" if torch.cuda.is_available() else "cpu"
processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-base").to(device)

# Hugging Face API 설정
HUGGINGFACE_API_KEY = os.getenv("HF_TOKEN")  # <- Hugging Face에서 발급받은 토큰으로 교체
HF_API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.1"
HF_HEADERS = {
    "Authorization": f"Bearer {HUGGINGFACE_API_KEY}",
    "Content-Type": "application/json"
}


def translate_text_mtranslate(text, src_lang="en", dest_lang="ko"):
    """Mtranslate 라이브러리를 사용하여 영어 → 한국어 번역"""
    translated_text = translate(text, dest_lang, src_lang)
    return clean_repeated_words(translated_text)

def clean_repeated_words(text):
    """반복되는 단어 제거"""
    text = re.sub(r'(\b\w+\b)( \1)+', r'\1', text)
    return text.strip().capitalize()

def translate_to_korean(text):
    """Mtranslate 라이브러리를 사용하여 번역"""
    return translate_text_mtranslate(text)

def download_image(image_url):
    """GCS URL에서 이미지 다운로드"""
    response = requests.get(image_url, stream=True)
    if response.status_code == 200:
        image = Image.open(response.raw).convert("RGB")
        return image
    else:
        return None

def generate_caption(image):
    """BLIP 모델을 사용하여 자동 캡션 생성"""
    inputs = processor(images=image, return_tensors="pt").to(device)
    with torch.no_grad():
        output = model.generate(**inputs)
    caption = processor.decode(output[0], skip_special_tokens=True)
    return caption

def clean_text(text):
    text = re.sub(r'\u200b', '', text)
    text = re.sub(r'(\b\w+\b)( \1)+', r'\1', text)
    return text.strip()

def generate_movie_story(blip_description):
    """Mistral-7B API (via Hugging Face)로 영화 줄거리 생성"""
    prompt = f"""
    🎬 영화 줄거리 생성 요청 🎬

    🔹 이 영화의 주요 장면은 다음과 같습니다:
    {blip_description}

    🔹 영화의 제목을 가정하고, 등장인물의 이름과 역할을 설정하세요.
    🔹 이 장면을 기반으로 창의적인 영화 줄거리를 작성하세요.
    🔹 영화의 시작, 중반, 결말을 포함하도록 해 주세요.

    📌 **출력 형식 예시 (반드시 이 형식을 따르세요)**:
    ---
    제목: [영화 제목]
    등장인물: [주인공, 조연 등]
    줄거리: [줄거리]

    ---
    반드시 위의 출력 형식을 그대로 유지하여 작성해주세요.
    """

    time.sleep(1)

    payload = {
        "inputs": prompt, #strip() 추가시 프롬포트 한줄로 출력
        "parameters": {
            "max_new_tokens": 1500,
            "temperature": 0.7,
            "top_p": 0.9,
            "return_full_text": False
        }
    }

    response = requests.post(HF_API_URL, headers=HF_HEADERS, json=payload)
    if response.status_code == 200:
        result = response.json()
        return result[0]["generated_text"]
    else:
        return f"API 요청 실패: {response.status_code}\n응답: {response.text}"


@app.route("/ping", methods=["GET"])
def ping():
    return "pong", 200

@app.route("/generate_story", methods=["POST"])
def generate_story():
    try:
        app.logger.info("📩 Story generation 요청 도착")

        if not request.is_json:
            app.logger.error("🚨 요청이 JSON 형식이 아님")
            return jsonify({"error": "Invalid JSON format"}), 400

        data = request.json
        image_urls = data.get("image_urls", [])
        movie_list_id = data.get("movieListId")

        if not image_urls:
            app.logger.warning("⚠️ image_urls 비어 있음")
            return jsonify({"error": "No image URLs provided"}), 400
        
        captions = []
        for url in image_urls:
            app.logger.info(f"🖼️ 이미지 다운로드 중: {url}")
            image = download_image(url)
            if image:
                caption = generate_caption(image)
                app.logger.info(f"📸 캡션 생성 완료: {caption}")
                captions.append(clean_text(caption))
            else:
                app.logger.error(f"❌ 이미지 다운로드 실패: {url}")
                return jsonify({"error": f"Failed to download image from {url}"}), 400

        combined_description = " ".join(captions)
        app.logger.info(f"🧠 생성할 전체 설명: {combined_description}")

        generated_story = generate_movie_story(combined_description)
        app.logger.info("📖 영어 줄거리 생성 완료")

        translated_story = translate_to_korean(generated_story)
        app.logger.info("🌐 한국어 번역 완료")

        response_data = {
            "movieListId": movie_list_id,
            "generated_story": translated_story
        }

        app.logger.info("✅ 줄거리 생성 응답 완료")
        return jsonify(response_data)

    except Exception as e:
        app.logger.exception("🔥 예외 발생:")
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
