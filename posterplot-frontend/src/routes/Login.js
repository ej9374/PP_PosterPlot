import { React, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import authstyle from "../styles/AuthStyle.module.css";

import icon_closed_eye from "../icons/icon-closed-eyes-5264104.png";
import icon_eye from "../icons/icon-eye-5264291.png";

function Login() {
  // 유저 정보
  const [userInfo, setUserInfo] = useState({
    id: "",
    password: "",
  });

  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  // 아이디, 비밀번호
  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setUserInfo((userInfo) => ({
      ...userInfo,
      [name]: value,
    }));
  };

  const handleLogin = async (event) => {
    if (isLoading) return; // 이미 요청 중이면 중복 요청 방지
    setIsLoading(true); // 로딩 시작

    event.preventDefault();

    if (!(userInfo.id && userInfo.password)) {
      alert("아이디와 비밀번호를 모두 입력해주세요.");
      setIsLoading(false);
      return;
    }

    const { id, password } = userInfo;

    try {
      const response = await axios.post(
        `${process.env.REACT_APP_BACKEND_URL}/auth/login`,
        {
          id,
          password,
        }
      );
      if (response.status === 200) {
        localStorage.setItem("token", response.data); // JWT 토큰 저장
        localStorage.setItem("userId", userInfo.id);
        alert("로그인되었습니다.");
        navigate("/");
      }
    } catch (error) {
      console.error("로그인 실패:", error.response?.data || error.message);
      alert("아이디 또는 비밀번호가 틀렸습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={`${display.xyCenter} ${authstyle.container}`}>
      <div>
        <h1 className={`${display.titleFont}`} align="center">
          로그인
        </h1>
        <div>
          <form
            className={`${display.nameFont}`}
            onChange={handleInputChange}
            onSubmit={handleLogin}
          >
            <div style={{ margin: "10px" }}>
              <input
                type="text"
                maxLength={20}
                value={userInfo.id}
                name="id"
                placeholder="아이디를 입력해주세요"
              />
            </div>
            <div style={{ margin: "10px" }}>
              <input
                type={showPassword ? "text" : "password"}
                maxLength={20}
                value={userInfo.password}
                name="password"
                placeholder="비밀번호를 입력해주세요"
              />
              <button
                type="button"
                className={display.clickable}
                onClick={(event) => {
                  event.preventDefault();
                  setShowPassword(!showPassword);
                }} // 버튼 클릭 시 상태 변경
                style={{
                  position: "absolute",
                  background: "none",
                  border: "none",
                  padding: "7.5px",
                }}
              >
                <img
                  src={showPassword ? icon_eye : icon_closed_eye}
                  style={{
                    width: "20px",
                    height: "20px",
                    objectFit: "contain",
                  }}
                />{" "}
                {/* 아이콘 변경 */}
              </button>
            </div>
            <div align="center">
              <button type="submit" className={authstyle.button}>
                로그인
              </button>
            </div>
          </form>
        </div>
        <div
          style={{
            padding: "30px 0 0 0", // 위, 아래 padding 10px
            display: "flex",
            flexDirection: "column",
            alignItems: "center", // 자식 요소 가운데 정렬
            textAlign: "center", // 텍스트 가운데 정렬
          }}
        >
          <p style={{ margin: "0", color: "grey" }}>계정이 없으신가요?</p>
          <a style={{ color: "#ffffff" }} href="/signup">
            회원가입
          </a>
        </div>
      </div>
    </div>
  );
}

export default Login;
