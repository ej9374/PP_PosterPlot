import { React, useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import createstyle from "../styles/CreateStyle.module.css";

import noImage from "../icons/no-image-movie.jpg";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

// AI Text 화면 출력
const TypingText = ({ text, speed }) => {
  const [displayedText, setDisplayedText] = useState("");

  useEffect(() => {
    setDisplayedText(""); // 초기화
    let isCancelled = false;

    const typeText = (i) => {
      if (isCancelled) return;
      if (i < text.length) {
        setDisplayedText((prev) => prev + text[i]);
        setTimeout(() => typeText(i + 1), speed);
      }
    };

    typeText(0); // 재귀 호출 시작

    return () => {
      isCancelled = true; // 컴포넌트가 언마운트되면 실행 중지
    };
  }, [text, speed]);

  return (
    <p style={{ lineHeight: "2", textAlign: "justify" }}>{displayedText}</p>
  );
};

function UploadPoster() {
  const [files, setFiles] = useState([]);
  const [posterUrls, setPosterUrls] = useState("");

  const [loading, setLoading] = useState(false);

  const [AItext, setAItext] = useState("");

  const navigate = useNavigate();
  const bottomRef = useRef(null);

  const processMoviePoster = async (formData) => {
    if (loading) return;
    setLoading(true);

    let movieListId = null;
    let aiStoryId = null;

    try {
      // upload poster -> fetch movieListid
      const uploadResponse = await axios.post(
        `${BACKEND_URL}/movie/upload`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      const match = uploadResponse.data.match(/movieListId\s*=\s*(\d+)/);
      movieListId = match ? parseInt(match[1], 10) : null;
      if (!movieListId) throw new Error("movieListId를 추출할 수 없습니다.");

      console.log("movieListId:", movieListId);

      // upload movieListId -> fetch AI Story id
      const storyResponse = await axios.post(
        `${BACKEND_URL}/movie/getStory`,
        new URLSearchParams({ movieListId }),
        {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      aiStoryId = storyResponse.data;
      if (!aiStoryId) throw new Error("aiStoryId를 가져올 수 없습니다.");

      console.log("aiStoryId:", aiStoryId);

      // upload aiStoryId -> fetch AI Story Text
      const textResponse = await axios.get(`${BACKEND_URL}/movie/aiStory`, {
        params: { aiStoryId },
      });

      console.log("AI Story Text:", textResponse.data);
      setAItext(textResponse.data);
    } catch (error) {
      console.error("Error processing movie poster:", error);
    } finally {
      setLoading(false);
    }
  };

  const onClickPosterUpload = () => {
    if (files.length >= 2) {
      alert("이미지는 2개까지 올릴 수 있습니다.");
    }
    return;
  };

  const onChangePosterUpload = (event) => {
    const selectedFiles = event.target.files;

    if (selectedFiles && selectedFiles.length > 0) {
      // files에 선택한 이미지 저장
      const updatedFiles = [...files, ...Array.from(selectedFiles)];
      if (updatedFiles.length > 2) {
        alert("이미지는 2개까지 올릴 수 있습니다.");
        return;
      }
      setFiles(updatedFiles);

      // posterUrl에 선택한 이미지 url 저장
      for (let i = 0; i < selectedFiles.length; i++) {
        const reader = new FileReader();
        reader.readAsDataURL(selectedFiles[i]);
        reader.onloadend = () => {
          setPosterUrls((prevUrls) => {
            const newUrls = [...prevUrls, reader.result]; // base64 인코딩된 이미지 저장
            return newUrls;
          }); // base64 인코딩된 이미지 저장
        };
      }
    }
  };

  const deleteUploadedPoster = () => {
    setFiles([]);
    setPosterUrls("");
  };

  const handlePostCreation = async () => {
    if (files.length !== 2) {
      alert("포스터는 2개 올릴 수 있습니다.");
      return;
    }

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append("files", files[i]);
    }

    setAItext(processMoviePoster(formData));
  };

  useEffect(() => {
    if (AItext) {
      bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [AItext]);

  const handleNavigate = () => {
    navigate("/createpost", {
      state: { uploadedPoster: posterUrls, AItext: AItext },
    });
  };

  return (
    <div>
      <div className={createstyle.container}>
        <div align="center">
          <div>
            <h1 className={display.titleFont} style={{ margin: "5px" }}>
              포스터 등록하기
            </h1>
            <p className={display.nameFont}>
              포스터를 등록하고 AI 줄거리를 생성해보세요!
            </p>
          </div>
          <div>
            <img
              src={posterUrls[0] ? posterUrls[0] : noImage} // 첫 번째 포스터
              img="img"
            />
            <img
              src={posterUrls[1] ? posterUrls[1] : noImage} // 두 번째 포스터
              img="img"
            />
          </div>
          <p className={display.nameFont}>※ 2장의 포스터를 선택해주세요!</p>
          <div>
            {posterUrls && (
              <button
                onClick={deleteUploadedPoster}
                className={createstyle.initializeButton}
              >
                초기화
              </button>
            )}
            <div>
              <input
                type="file"
                accept="image/*"
                multiple={true}
                onChange={onChangePosterUpload}
                disabled={files.length >= 2}
                id="file-input"
                style={{ display: "none" }} // 기본 input 숨김
              />
              <label
                onClick={onClickPosterUpload}
                htmlFor="file-input"
                className={`${display.clickable} ${createstyle.fileButton}`}
              >
                파일 선택
              </label>
            </div>
            <button
              disabled={posterUrls.length !== 2}
              onClick={handlePostCreation}
              className={`${display.titlefont} ${createstyle.AIButton}`}
            >
              AI 줄거리 생성하기
            </button>
            {loading && (
              <div className={`${display.nameFont} ${createstyle.AItext}`}>
                <h3 style={{ color: "#aaaaaa" }}>잠시만 기다려주세요...</h3>
              </div>
            )}
            {!loading && AItext && (
              <div>
                <div className={`${display.nameFont} ${createstyle.AItext}`}>
                  <h3 style={{ color: "#aaaaaa" }}>
                    포스터를 기반으로 AI가 분석한 줄거리입니다!
                  </h3>
                  <TypingText text={AItext} speed={20} />
                </div>
                <button
                  className={createstyle.AIButton}
                  onClick={handleNavigate}
                  ref={bottomRef}
                >
                  시나리오 작성하기
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default UploadPoster;
