import { React, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import createstyle from "../styles/CreateStyle.module.css";
import homestyle from "../styles/HomeStyle.module.css";

import icon_moon_poster from "../icons/free-icon-poster-659313.png";
import icon_film_poster from "../icons/free-icon-poster-4133180.png";
import icon_writing from "../icons/free-icon-writing-2728981.png";
import icon_robot from "../icons/free-icon-robot-4105192.png";
import icon_speech_bubble from "../icons/free-icon-speech-bubble-2496476.png";
import noImage from "../icons/no-image-movie.jpg";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

const fetchTopLikesPosts = async () => {
  try {
    const response = await axios.get(`${BACKEND_URL}/posts/home/top_likes`);
    return response.data;
  } catch (error) {
    console.error("Error fetching top likes posts:", error);
  }
};

function Home() {
  const navigate = useNavigate();

  const [top3posts, setTop3posts] = useState([{}, {}, {}]);

  useEffect(() => {
    const elements = document.querySelectorAll(`.${display.fadeIn}`);
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add(display.visible);
          } else {
            entry.target.classList.remove(display.visible); // 화면에서 벗어나면 사라짐
          }
        });
      },
      { threshold: 0.3 }
    );
    elements.forEach((el) => observer.observe(el));

    return () => {
      elements.forEach((el) => observer.unobserve(el));
    };
  }, []);

  useEffect(() => {
    const fetchTopLikesPosts = async () => {
      try {
        const response = await axios.get(`${BACKEND_URL}/posts/home/top_likes`);
        console.log(response.data);
        setTop3posts(response.data);
      } catch (error) {
        console.error("Error fetching top likes posts:", error);
      }
    };
    fetchTopLikesPosts();
  }, []);

  return (
    <div className={`${homestyle.container}`}>
      <div className={homestyle.greetingSection}>
        <h1 className={display.titleFont}>
          안녕하세요!
          <br />
          PosterPlot입니다.
        </h1>
        <p className={display.nameFont}>
          PosterPlot은 AI와 함께 영화 시나리오를 만들 수 있는 서비스입니다.
          <br />
          <br />
          영화 포스터를 업로드하여 AI가 생성한 시나리오를 만들고 <br />
          자신만의 이야기를 펼쳐 보세요!
        </p>
      </div>
      <div className={`${display.nameFont} ${homestyle.guideSection}`}>
        <div className={`${homestyle.guide} ${display.fadeIn}`}>
          <h2>1. 영화 포스터를 고르세요.</h2>
          <p>AI에게 분석을 맡길 영화 포스터를 업로드해주세요.</p>
          <div>
            <img src={icon_moon_poster}></img>
            <img src={icon_film_poster}></img>
          </div>
        </div>
        <div className={`${homestyle.guide} ${display.fadeIn}`}>
          <h2>2. AI가 만들어준 시나리오를 확인하세요.</h2>
          <p>포스터를 바탕으로 AI가 영화 시나리오를 상상해서 출력해줍니다!</p>
          <div>
            <img src={icon_writing}></img>
            <img src={icon_robot}></img>
          </div>
        </div>
        <div className={`${homestyle.guide} ${display.fadeIn}`}>
          <h2>3. 시나리오를 게시판에 올리세요.</h2>
          <p>AI가 생성한 시나리오에 대한 의견을 공유해보세요!</p>
          <div>
            <img src={icon_speech_bubble} />
          </div>
        </div>
      </div>
      <div className={display.fadeIn}>
        <div>
          <h1>TOP 3</h1>
          <p>가장 좋아요를 많이 받은 시나리오입니다.</p>
        </div>
        <div className={homestyle.top3Container}>
          <div className={display.clickable}>
            <img
              src={top3posts[0] ? top3posts[0].movie1stPath : noImage}
              style={{ width: "100%", height: "auto" }}
              onClick={() => navigate(`/post/${top3posts[0].postId}`)}
              img="img"
            ></img>
            <h3>{top3posts[0] ? top3posts[0].title : `제목 없음`}</h3>
            <p>{top3posts[0] ? top3posts[0].id : `아이디`}</p>
          </div>
          <div className={display.clickable}>
            <img
              src={top3posts[1] ? top3posts[1].movie1stPath : noImage}
              style={{ width: "100%", height: "auto" }}
              onClick={() => navigate(`/post/${top3posts[1].postId}`)}
              img="img"
            ></img>
            <h3>{top3posts[1] ? top3posts[1].title : `제목 없음`}</h3>
            <p>{top3posts[1] ? top3posts[1].id : `아이디`}</p>
          </div>
          <div className={display.clickable}>
            <img
              src={top3posts[2] ? top3posts[2].movie1stPath : noImage}
              style={{ width: "100%", height: "auto" }}
              onClick={() => navigate(`/post/${top3posts[2].postId}`)}
              img="img"
            ></img>
            <h3>{top3posts[2] ? top3posts[2].title : `제목 없음`}</h3>
            <p>{top3posts[2] ? top3posts[2].id : `아이디`}</p>
          </div>
        </div>
        <p className={display.clickable} onClick={() => navigate("/postlist")}>
          모든 시나리오 보러 가기
        </p>
      </div>
      {!!localStorage.getItem("token") && !!localStorage.getItem("userId") ? (
        <button
          className={`${createstyle.AIButton} ${display.fadeIn}`}
          onClick={() => navigate("/uploadposter")}
        >
          시나리오 만들기
        </button>
      ) : (
        <button
          className={`${createstyle.AIButton} ${display.fadeIn}`}
          onClick={() => navigate("/login")}
        >
          로그인하기
        </button>
      )}
    </div>
  );
}

export default Home;
