import { jwtDecode } from "jwt-decode";

// 토큰 만료 여부 확인 함수
const isTokenExpired = () => {
  const token = localStorage.getItem("token");
  if (token) {
    try {
      const decoded = jwtDecode(token);
      const now = Date.now() / 1000; // 현재 시간 (초 단위)
      return decoded.exp < now; // 만료 시간이 현재 시간보다 이전이면 true
    } catch (error) {
      return true; // 토큰이 유효하지 않으면 만료된 것으로 처리
    }
  }
};

export default isTokenExpired;
