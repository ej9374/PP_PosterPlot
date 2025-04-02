import { useEffect } from "react";
import { useLocation, Outlet } from "react-router-dom";
import HeaderLoggedIn from "./HeaderLoggedIn";
import HeaderLoggedOut from "./HeaderLoggedOut";

const Layout = () => {
  const location = useLocation();
  const isLogin =
    !!localStorage.getItem("token") && !!localStorage.getItem("userId"); // 로그인 상태 확인

  useEffect(() => {
    // 라우트 변경될 때마다 실행
    console.log("Route changed:", location.pathname);
  }, [location]);

  return (
    <div>
      {isLogin ? <HeaderLoggedIn /> : <HeaderLoggedOut />}
      <Outlet />
    </div>
  );
};

export default Layout;
