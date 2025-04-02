import { React, useState, useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import createstyle from "../styles/CreateStyle.module.css";

import noImage from "../icons/no-image-movie.jpg";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

function CreatePost() {
  const navigate = useNavigate();
  const location = useLocation();
  const uploadedPoster = location.state?.uploadedPoster || null;
  const generatedAItext = location.state?.AItext || null;
  const aiStoryId = location.state?.aiStoryId || null;

  const [isLoading, setIsLoading] = useState(false);

  const [movieTitle, setMovieTitle] = useState("");
  const [userText, setUserText] = useState("");
  const [AItext, setAItext] = useState(generatedAItext);

  const [selectedGenre, setSelectedGenre] = useState("unselected"); // 선택된 장르 저장

  const genres = [
    { value: "action", label: "액션" },
    { value: "crime", label: "범죄" },
    { value: "romance", label: "로맨스" },
    { value: "sci_fi", label: "SF" },
    { value: "comedy", label: "코미디" },
    { value: "sports", label: "스포츠" },
    { value: "fantasy", label: "판타지" },
    { value: "music", label: "음악" },
    { value: "musical", label: "뮤지컬" },
    { value: "war", label: "전쟁" },
    { value: "horror", label: "호러" },
    { value: "thriller", label: "스릴러" },
  ];

  const AITextRef = useRef(null);
  const userTextRef = useRef(null);

  // 페이지 로드 시 스크롤 위치 초기화
  useEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: "smooth" });
  }, []);

  const handleTitleInputChange = (event) => {
    setMovieTitle(event.target.value);
  };

  const handleAIInputChange = (event) => {
    setAItext(event.target.value);
    adjustHeight(AITextRef);
  };

  const handleUserInputChange = (event) => {
    setUserText(event.target.value);
    adjustHeight(userTextRef);
  };

  const adjustHeight = (ref) => {
    if (ref.current) {
      const newHeight = Math.max(250, ref.current.scrollHeight); // 최소 높이 250px
      ref.current.style.height = newHeight + "px";
    }
  };

  const submitPost = async () => {
    if (isLoading) return;
    setIsLoading(true);

    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    if (!token || !userId) {
      alert("로그인이 필요합니다.");
      setIsLoading(false);
      return;
    }

    if (!(movieTitle && userText && selectedGenre)) {
      alert("작성하지 않은 항목이 있습니다.");
      return;
    }

    const postData = {
      title: movieTitle,
      content: userText,
      genre: selectedGenre,
      aiStoryId: aiStoryId,
    };
    try {
      const response = await axios.post(
        `${BACKEND_URL}/posts/create`,
        postData,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`, // 인증 헤더 추가
          },
        }
      );

      setIsLoading(false);
      alert("게시글이 성공적으로 업로드되었습니다.");
      navigate("/postlist");
    } catch (error) {
      console.error("게시글 업로드 실패:", error);
      setIsLoading(false);
    }
  };

  return (
    <div>
      <div className={createstyle.container}>
        <div align="center" style={{ width: "800px" }}>
          <h1 className={display.titleFont}>스토리 작성하기</h1>
          <div>
            <div>
              <form>
                <div style={{ margin: "40px" }}>
                  <img
                    src={uploadedPoster?.[0] || noImage}
                    style={{ width: "280px", height: "400px" }}
                    img="img"
                  />
                  <img
                    src={uploadedPoster?.[1] || noImage}
                    style={{ width: "280px", height: "400px" }}
                    img="img"
                  />
                </div>
                <div>
                  <label
                    className={display.titleFont}
                    style={{ margin: "10px", fontSize: "130%" }}
                  >
                    장르 선택
                  </label>
                  <div className={createstyle.genreButtons}>
                    {genres.map((genre) => (
                      <button
                        type="button"
                        key={genre.value}
                        className={`${display.titleFont} ${display.clickable} ${
                          createstyle.genreButton
                        } ${
                          selectedGenre === genre.value
                            ? createstyle.selected
                            : ""
                        }`}
                        onClick={(event) => {
                          event.preventDefault();
                          if (genre.value) {
                            setSelectedGenre(genre.value);
                          }
                        }}
                      >
                        {genre.label}
                      </button>
                    ))}
                  </div>
                </div>
                <div style={{ margin: "40px" }}>
                  <input
                    className={`${display.titleFont} ${createstyle.postingTitleInput}`}
                    value={movieTitle}
                    onChange={handleTitleInputChange}
                    placeholder="영화 제목 (예: 에브리씽 에브리웨어 올 앳 원스)"
                  />
                  <textarea
                    ref={AITextRef}
                    className={`${display.nameFont} ${createstyle.textarea}`}
                    style={{ color: "grey" }}
                    value={AItext}
                    onChange={handleAIInputChange}
                    placeholder="AI STORY"
                  />
                  <textarea
                    ref={userTextRef}
                    className={`${display.nameFont} ${createstyle.textarea}`}
                    value={userText}
                    onChange={handleUserInputChange}
                    placeholder="AI가 생성한 STORY에 대한 의견을 자유롭게 적어주세요!"
                  />
                </div>
                <button
                  type="button"
                  onClick={submitPost}
                  className={createstyle.AIButton}
                >
                  게시하기
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CreatePost;
