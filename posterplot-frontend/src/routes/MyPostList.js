import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import liststyle from "../styles/ListStyle.module.css";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

function PostList() {
  const navigate = useNavigate();

  const [posts, setPosts] = useState([]);
  // server에서 불러온 posts

  // fetch post list (sortOption)
  const fetchMyPostList = async () => {
    try {
      const response = await axios.get(`${BACKEND_URL}/posts/list/my`, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });

      setPosts(response.data); // 성공적으로 데이터를 받으면 반환
    } catch (error) {
      console.error("Error fetching post list:", error.message);
      setPosts([]);
    }
  };

  // 초기 로드 시 실행 (sortOption 변경될 때도 실행)
  useEffect(() => {
    fetchMyPostList();
  }, []);

  return (
    <div className={liststyle.container}>
      <div
        className={display.titleFont}
        style={{
          textAlign: "left",
        }}
      >
        <h1 style={{ margin: "0px 5px" }}>내가 쓴 글</h1>
        <p style={{ margin: "5px" }}>
          {localStorage.getItem("userId")}님이 올린 시나리오 목록입니다.
        </p>
      </div>
      <div
        className={liststyle.postlist}
        style={{
          borderTop: "2px grey solid",
          padding: "20px 0px",
        }}
      >
        <div className={liststyle.post}>
          <p>번호</p>
          <p>제목</p>
          <p>아이디</p>
        </div>
        {posts.length === 0 ? (
          <p>작성한 게시글이 없습니다.</p>
        ) : (
          posts.map((post) => (
            <div
              className={`${display.clickable} ${liststyle.post}`}
              onClick={() => navigate(`/post/${post.postId}`)}
            >
              <p>{post.postId}</p>
              <p>{post.title}</p>
              <p>{post.id}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default PostList;
