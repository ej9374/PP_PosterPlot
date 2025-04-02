import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import liststyle from "../styles/ListStyle.module.css";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

function PostList() {
  const navigate = useNavigate();

  const [selectedGenre, setSelectedGenre] = useState("unselected"); // 선택된 장르 저장
  const genres = [
    { value: "ACTION", label: "액션" },
    { value: "CRIME", label: "범죄" },
    { value: "ROMANCE", label: "로맨스" },
    { value: "SCI_FI", label: "SF" },
    { value: "COMEDY", label: "코미디" },
    { value: "SPORTS", label: "스포츠" },
    { value: "FANTASY", label: "판타지" },
    { value: "MUSIC", label: "음악" },
    { value: "MUSICAL", label: "뮤지컬" },
    { value: "WAR", label: "전쟁" },
    { value: "HORROR", label: "호러" },
    { value: "THRILLER", label: "스릴러" },
  ];

  const [posts, setPosts] = useState([]);
  // server에서 불러온 posts

  const [sortOption, setSortOption] = useState("latest");

  // fetch post list (sortOption)
  const fetchPostListBySortOption = async (sortOption) => {
    try {
      const response = await axios.get(`${BACKEND_URL}/posts/list`, {
        params: { type: sortOption },
      });

      setPosts(response.data); // 성공적으로 데이터를 받으면 반환
    } catch (error) {
      console.error("Error fetching post list:", error);
      setPosts([]);
    }
  };

  // fetch post list (genre)
  const fetchPostListByGenre = async (selectedGenre) => {
    try {
      const response = await axios.get(
        `${BACKEND_URL}/posts/list/${selectedGenre}`,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      setPosts(response.data);
    } catch (error) {
      console.error("Error fetching genre posts:", error);
    }
  };

  const handleSortOptionChange = (event) => {
    const selectedValue = event.target.value;
    setSortOption(selectedValue);
    setSelectedGenre("unselected");
  };

  // 초기 로드 시 실행 (sortOption, selectedGenre 변경될 때도 실행)
  useEffect(() => {
    if (selectedGenre === "unselected") {
      fetchPostListBySortOption(sortOption);
    } else {
      fetchPostListByGenre(selectedGenre);
    }
  }, [sortOption, selectedGenre]);

  return (
    <div className={liststyle.container}>
      <div
        className={display.titleFont}
        style={{
          textAlign: "left",
        }}
      >
        <h1 style={{ margin: "0px 5px" }}>게시판</h1>
        <p style={{ margin: "5px" }}>
          다른 유저들이 올린 시나리오를 볼 수 있습니다.
        </p>
      </div>
      <div
        style={{
          borderTop: "2px grey solid",
          borderBottom: "2px grey solid",
          padding: "20px 0px",
        }}
      >
        <div style={{ display: "flex", gap: "5px", marginBottom: "10px" }}>
          <button
            className={`${display.titleFont} ${display.clickable} ${
              liststyle.genreButton
            } ${selectedGenre === "unselected" ? liststyle.selectedGenre : ""}`}
            style={{ width: "280px" }}
            onClick={(event) => {
              event.preventDefault();
              setSelectedGenre("unselected");
            }}
          >
            모든 장르
          </button>
          <div>
            <select
              className={`${display.titleFont} ${liststyle.select}`}
              value={sortOption}
              onChange={handleSortOptionChange}
            >
              <option value="latest">최신순</option>
              <option value="oldest">날짜순</option>
              <option value="likes">인기순</option>
            </select>
          </div>
        </div>
        <div className={liststyle.genreButtons}>
          {genres.map((genre) => (
            <button
              type="button"
              key={genre.value}
              className={`${display.titleFont} ${display.clickable} ${
                liststyle.genreButton
              } ${
                selectedGenre === genre.value ? liststyle.selectedGenre : ""
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
      <div className={liststyle.postlist}>
        <div className={liststyle.post}>
          <p>번호</p>
          <p>제목</p>
          <p>아이디</p>
        </div>
        {posts.map((post) => (
          <div
            className={`${display.clickable} ${liststyle.post}`}
            onClick={() => navigate(`/post/${post.postId}`)}
          >
            <p>{post.postId}</p>
            <p>{post.title}</p>
            <p>{post.id}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default PostList;
