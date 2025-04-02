import React from "react";
import { useNavigate } from "react-router-dom";

import display from "../styles/Display.module.css";
//import './Header.css';

const HeaderLoggedIn = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");

    navigate("/");
  };

  return (
    <header className={`${display.header} ${display.nameFont}`}>
      <h1
        className={`${display.clickable} ${display.titleFont}`}
        onClick={() => navigate("/")}
      >
        PosterPlot
      </h1>
      <div>
        <h3 style={{ color: "grey" }}>
          안녕하세요, {localStorage.getItem("userId")} 님!
        </h3>
        <p
          className={`${display.clickable}`}
          onClick={() => navigate("/uploadposter")}
        >
          포스터 등록
        </p>
        <p
          className={`${display.clickable}`}
          onClick={() => navigate("/postlist")}
        >
          게시판
        </p>
        <p
          className={`${display.clickable}`}
          onClick={() => navigate("/mypostlist")}
        >
          내가 쓴 글
        </p>
        <p className={`${display.clickable}`} onClick={() => handleLogout()}>
          로그아웃
        </p>
      </div>
    </header>
  );
};

export default HeaderLoggedIn;
