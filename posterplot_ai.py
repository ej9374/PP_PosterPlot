from flask import Flask, request, jsonify
import torch
from transformers import BlipProcessor, BlipForConditionalGeneration
from PIL import Image
import requests
from mtranslate import translate
import re
from flask_cors import CORS # 추가
import time



app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})    # 추가가

#  모델 로드
device = "cuda" if torch.cuda.is_available() else "cpu"
processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-base").to(device)

#  OpenRouter API 설정
OPENROUTER_API_KEY = "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"
API_URL = "https://openrouter.ai/api/v1/chat/completions"
HEADERS = {
    "Authorization": f"Bearer {OPENROUTER_API_KEY}",
    "Content-Type": "application/json",
    "X-Title": "Movie Story Generator"
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
    text = text.strip() 
    return text

def generate_movie_story(blip_description):

    """Mistral-7B API를 사용하여 영화 줄거리 생성"""
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
    
    time.sleep(1) #속도제한회피

    data = {
        "model": "mistralai/mistral-7b-instruct:free",
        "messages": [{"role": "user", "content": prompt}],
        "max_tokens": 1500,
        "temperature": 0.7,
        "top_p": 0.9,
    }
    
    response = requests.post(API_URL, headers=HEADERS, json=data)
    if response.status_code == 200:
        completion = response.json()
        return completion['choices'][0]['message']['content']
    else:
        return f"API 요청 실패: {response.status_code}\\n응답: {response.text}"

@app.route("/generate_story", methods=["POST"])
def generate_story():
    """GCS URL을 받아 BLIP과 Mistral-7B를 사용해 줄거리 생성"""

    if not request.is_json:
        print("🚨 ERROR: 요청이 JSON 형식이 아님!")
        return jsonify({"error": "Invalid JSON format"}), 400

    
    data = request.json
    image_urls = data.get("image_urls", [])  # GCS URL 리스트 >>>수정하기<<<
    movie_list_id = data.get("movieListId")  # movieListId 받기
    
    if not image_urls:
        print("⚠️ No image URLs provided")
        return jsonify({"error": "No image URLs provided"}), 400
    
    captions = []
    for url in image_urls:
        image = download_image(url)
        if image:
            caption = generate_caption(image)
            captions.append(clean_text(caption))
        else:
            return jsonify({"error": f"Failed to download image from {url}"}), 400
    
    combined_description = " ".join(captions)
    generated_story = generate_movie_story(combined_description)
    translated_story = translate_to_korean(generated_story)
    
    response_data = {
        "movieListId": movie_list_id,
        "generated_story": translated_story
    }
    return jsonify(response_data)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
