import React from "react";
import { useNavigate } from "react-router-dom";

import display from "../styles/Display.module.css";
//import './Header.css';

const HeaderLoggedOut = () => {
  const navigate = useNavigate();
  return (
    <header className={`${display.header}`}>
      <h1
        className={`${display.clickable} ${display.titleFont}`}
        onClick={() => navigate("/")}
      >
        PosterPlot
      </h1>
      <div>
        <p
          className={`${display.clickable}`}
          onClick={() => navigate("/login")}
        >
          로그인
        </p>
        <p
          className={`${display.clickable}`}
          onClick={() => navigate("/signup")}
        >
          회원가입
        </p>
        <p
          className={`${display.clickable}`}
          onClick={() => navigate("/postlist")}
        >
          게시글
        </p>
      </div>
    </header>
  );
};

export default HeaderLoggedOut;
