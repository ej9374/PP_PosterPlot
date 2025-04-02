import { React, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import authstyle from "../styles/AuthStyle.module.css";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

function Signup() {
  // 유저 정보
  const [userInfo, setUserInfo] = useState({
    id: "",
    email: "",
    code: "",
    password: "",
    passwordConfirm: "",
  });

  const [isIdDuplicated, setIsIdDuplicated] = useState(null); // 아이디 중복 여부
  const [isCodeCorrect, setIsCodeCorrect] = useState(false); // 인증번호 올바른지
  const [isEmailLoading, setIsEmailLoading] = useState(false); // 이메일 전송 로딩
  const [isLoading, setIsLoading] = useState(false); // 회원가입 로딩

  const navigate = useNavigate();

  // 6자 이상 20자 이하, 알파벳 대소문자와 숫자만 포함하는 경우 true 리턴
  const idValid = /^(?=.*[a-zA-Z])[a-zA-Z0-9]{6,20}$/.test(userInfo.id);

  // 이메일 형태를 가진 경우 true 리턴
  const emailValid = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(
    userInfo.email
  );

  // 8자 이상 20자 이하, 하나 이상의 숫자, 하나 이상의 알파벳을 포함하는 경우 true 리턴
  const passwordValid = /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,15}$/.test(
    userInfo.password
  );

  // isVaild 변수 (boolean)
  const isVaild =
    idValid && // 올바른 형태의 아이디인가?
    isIdDuplicated === false && // 아이디가 중복되었는가?
    emailValid && // 올바른 형태의 이메일인가?
    userInfo.code && // 인증번호를 입력하였는가?
    passwordValid && // 올바른 형태의 비밀번호인가?
    userInfo.password === userInfo.passwordConfirm; // 비밀번호와 비밀번호 확인에 입력한 내용이 일치하는가?

  // 유저 정보 입력
  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setUserInfo((userInfo) => ({
      ...userInfo,
      [name]: value,
    }));
  };

  // 아이디 중복 체크
  const checkIdAvailability = async (id) => {
    if (!idValid) {
      setIsIdDuplicated(null);
      return;
    }
    try {
      const response = await axios.get(`${BACKEND_URL}/auth/checkId`, {
        params: { id: id },
        validateStatus: (status) => status === 200 || status === 409, // 200과 409를 정상 처리
      });
      if (response.status === 200) {
        setIsIdDuplicated(false); // 중복되지 않는 아이디
      } else if (response.status === 409) {
        setIsIdDuplicated(true); // 중복된 아이디
      }
    } catch (error) {
      console.error("Error checking ID:", error);
      setIsIdDuplicated(null);
    }
  };

  // 사용자가 입력한 내용이 변경될 때마다 아이디 유효성 확인
  useEffect(() => {
    checkIdAvailability(userInfo.id);
  }, [userInfo.id]);

  // 인증번호 메일 전송
  const sendVerificationEmail = async (email) => {
    setIsEmailLoading(true);
    try {
      const response = await axios.post(`${BACKEND_URL}/auth/mailSend`, null, {
        params: { email: email },
        validateStatus: (status) => status === 200 || status === 409, // 200과 409를 정상 처리
      });
      if (response.status === 200) {
        alert("인증번호가 발송되었습니다."); // 이메일 전송 완료
      } else if (response.status === 409) {
        alert("이미 사용중인 이메일입니다."); // 중복된 이메일
      }
    } catch (error) {
      console.error("Error sending email:", error);
    } finally {
      setIsEmailLoading(false);
    }
  };

  // 인증번호 일치 여부 확인
  const checkVerificationCode = async () => {
    try {
      const response = await axios.post(`${BACKEND_URL}/auth/mailAuthCheck`, {
        email: userInfo.email,
        authNum: userInfo.code,
      });
      if (response.status === 200) {
        setIsCodeCorrect(true);
        alert("인증번호가 확인되었습니다.");
        return true;
      }
    } catch (error) {
      setIsCodeCorrect(false);
      alert("인증번호가 일치하지 않습니다.");
      return false;
    }
  };

  // 회원 가입
  const handleSignUp = async (event) => {
    event.preventDefault();
    if (isLoading) return; // 이미 요청 중이면 중복 요청 방지

    setIsLoading(true); // 로딩 시작

    // 인증번호(code) 검증
    if (!isCodeCorrect) {
      alert("인증번호가 일치하지 않습니다.");
      return;
    }

    try {
      const response = await axios.post(
        `${BACKEND_URL}/auth/signUp`,
        userInfo,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.status === 201) {
        alert("회원가입이 완료되었습니다!");
        setTimeout(() => navigate("/Login"), 1000);
      }
    } catch (error) {
      console.error("회원가입 실패:", error.response?.data || error.message);
      alert(`회원가입 실패: ${error.response?.data.message || "서버 오류"}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      className={`${authstyle.signupContainer} ${display.xyCenter} ${authstyle.container}`}
    >
      <div>
        <h1 className={`${display.titleFont}`} align="center">
          회원가입
        </h1>
        <div>
          <form className={`${display.nameFont}`} onChange={handleInputChange}>
            <div style={{ margin: "15px", display: "flex" }}>
              <label>아이디</label>
              <div>
                <input
                  type="text"
                  placeholder="6자 이상의 영문 혹은 영문과 숫자를 조합"
                  maxLength={20}
                  value={userInfo.id}
                  name="id"
                />
                {isIdDuplicated === null && <p>아이디를 입력해주세요.</p>}
                {isIdDuplicated === false && <p>사용 가능한 아이디입니다.</p>}
                {isIdDuplicated === true && <p>이미 사용 중인 아이디입니다.</p>}
              </div>
            </div>
            <div style={{ margin: "15px", display: "flex" }}>
              <label>이메일</label>
              <div style={{ maxWidth: "450px" }}>
                <input
                  type="email"
                  placeholder="예: posterplot@plot.com"
                  value={userInfo.email}
                  name="email"
                />
                {isEmailLoading ? (
                  <button disabled="true">LOADING</button>
                ) : (
                  <button
                    disabled={!emailValid}
                    className={authstyle.authNumButton}
                    onClick={(event) => {
                      event.preventDefault();
                      sendVerificationEmail(userInfo.email);
                    }}
                  >
                    인증번호 받기
                  </button>
                )}
              </div>
            </div>
            <div style={{ margin: "15px" }}>
              <input
                style={{ marginLeft: "120px" }}
                type="string"
                placeholder="인증번호를 입력해주세요"
                value={userInfo.code}
                name="code"
              />
              <button
                style={{ marginLeft: "120px" }}
                disabled={!userInfo.code}
                className={authstyle.authNumButton}
                onClick={(event) => {
                  event.preventDefault();
                  checkVerificationCode();
                }}
              >
                인증번호 확인
              </button>
            </div>
            <div style={{ margin: "15px" }}>
              <label>비밀번호</label>
              <input
                type="password"
                placeholder="비밀번호를 입력해주세요"
                maxLength={15}
                value={userInfo.password}
                name="password"
              />
              <div style={{ marginLeft: "120px" }}>
                {passwordValid && <p>사용 가능한 비밀번호입니다.</p>}
              </div>
            </div>
            <div style={{ margin: "15px" }}>
              <label>비밀번호 확인</label>
              <input
                type="password"
                placeholder="비밀번호를 한번 더 입력해주세요"
                maxLength={15}
                value={userInfo.passwordConfirm}
                name="passwordConfirm"
              />
              <div style={{ marginLeft: "120px" }}>
                {userInfo.passwordConfirm &&
                  (userInfo.password === userInfo.passwordConfirm ? (
                    <p>비밀번호가 일치합니다</p>
                  ) : (
                    <p>비밀번호가 일치하지 않습니다</p>
                  ))}
              </div>
            </div>
          </form>
          <div align="center">
            <button
              disabled={!isVaild}
              className={authstyle.button}
              onClick={handleSignUp}
              style={{ marginTop: "20px" }}
            >
              가입하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Signup;
