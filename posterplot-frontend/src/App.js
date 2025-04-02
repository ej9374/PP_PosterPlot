import "./App.css";

import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import isTokenExpired from "./utils/auth.js";

import Layout from "./layout/Layout";
import RestrictedRoute from "./components/route/RestrictedRoute";
import PrivateRoute from "./components/route/PrivateRoute";

import Home from "./routes/Home";
import Signup from "./routes/Signup";
import Login from "./routes/Login";
import UploadPoster from "./routes/UploadPoster";
import CreatePost from "./routes/CreatePost";
import PostList from "./routes/PostList";
import Post from "./routes/Post";
import MyPostList from "./routes/MyPostList";

function App() {
  setInterval(() => {
    if (isTokenExpired()) {
      alert("세션이 만료되었습니다. 다시 로그인해주세요.");
      localStorage.removeItem("token");
      localStorage.removeItem("userId");
      window.location.href = "/login"; // 로그인 페이지로 이동
    }
  }, 1 * 60 * 1000); // 5분마다 체크
  return (
    <Router>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />
          {/** 홈 화면 */}
          <Route path="/postlist" element={<PostList />} />
          {/** 게시글 목록 화면 */}
          <Route path="/post/:postId" element={<Post />} />
          {/** 게시글 화면 */}

          {/** 로그인하지 않은 사용자만 접근 가능 */}
          <Route element={<RestrictedRoute />}>
            <Route path="/signup" element={<Signup />} />
            {/** 회원가입 화면 */}
            <Route path="/login" element={<Login />} />
            {/** 로그인 화면 */}
          </Route>

          {/** 로그인한 사용자만 접근 가능 */}
          <Route element={<PrivateRoute />}>
            <Route path="/uploadposter" element={<UploadPoster />} />
            {/** 포스터 업로드 화면 */}
            <Route path="/createpost" element={<CreatePost />} />
            {/** 게시글 작성 화면 */}
            <Route path="/mypostlist" element={<MyPostList />} />
            {/** 나의 게시글 목록 화면 */}
          </Route>
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
