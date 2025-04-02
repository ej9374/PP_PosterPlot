import React, { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import poststyle from "../styles/PostStyle.module.css";

import noImage from "../icons/no-image-movie.jpg";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

function Post() {
  const navigate = useNavigate();

  const { postId } = useParams();
  const [detail, setDetail] = useState([]);
  const [commentList, setCommentList] = useState([]);

  const [comment, setComment] = useState("");

  const fetchPostDetail = async (postId) => {
    try {
      const response = await axios.get(`${BACKEND_URL}/posts/view`, {
        params: { postId: postId },
      });

      setDetail([response.data]);
    } catch (error) {
      console.error("Error fetching post detail:", error);
      alert("게시글을 불러오는 중 오류가 발생했습니다.");
    }
  };

  const fetchCommentList = async (postId) => {
    try {
      const response = await axios.get(
        `${BACKEND_URL}/comment/list/${Number(postId)}`,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      setCommentList(response.data); // 성공적으로 데이터를 받으면 반환
    } catch (error) {
      console.error("Error fetching comments:", error);
      alert("댓글을 불러오는 중 오류가 발생했습니다.");
    }
  };

  const deletePost = async () => {
    try {
      const isConfirmed = window.confirm(
        "정말로 삭제하시겠습니까? 삭제한 게시글은 되돌릴 수 없습니다."
      );
      if (!isConfirmed) return;

      const token = localStorage.getItem("token");
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      console.log("Deleting post with ID:", postId);

      const response = await axios.delete(
        `${BACKEND_URL}/posts/delete/${Number(postId)}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      alert("삭제되었습니다.");
      navigate(-1);
    } catch (error) {
      console.error("Error deleting post:", error);
      alert("게시글 삭제에 실패했습니다.");
    }
  };

  const handleLike = async () => {
    try {
      const isConfirmed = window.confirm("'좋아요'는 취소할 수 없습니다.");
      if (!isConfirmed) return;

      const response = await axios.post(
        `${BACKEND_URL}/posts/like?postId=${postId}`,
        {},
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      // 좋아요 Button update
      fetchPostDetail(postId);
    } catch (error) {
      if (error.response && error.response.status === 400) {
        alert("이미 좋아요를 누른 게시글입니다.");
      } else {
        console.error("Error Adding Like:", error);
        alert("서버 에러가 발생했습니다.");
      }
    }
  };

  const handleCommentChange = (event) => {
    setComment(event.target.value);
  };

  const handleCommentSubmit = async (comment) => {
    if (comment.trim() === "") {
      alert("내용을 작성해주십시오.");
      return;
    }
    try {
      const response = await axios.post(
        `${BACKEND_URL}/comment/create?postId=${postId}&content=${encodeURIComponent(
          comment
        )}`,
        {},
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      alert("댓글이 성공적으로 작성되었습니다.");
      setComment("");
      fetchCommentList(postId); // 최신 댓글 목록을 다시 불러오기
    } catch (error) {
      if (error.response && error.response.status === 401) {
        alert("로그인 후 댓글을 작성할 수 있습니다.");
      } else {
        console.error("Error Adding comment:", error);
        alert("서버 에러가 발생했습니다.");
      }
    }
  };

  const deleteComment = async (commentId) => {
    try {
      const isConfirmed = window.confirm(
        "정말로 삭제하시겠습니까? 삭제한 댓글은 되돌릴 수 없습니다."
      );
      if (!isConfirmed) return;

      const response = await axios.delete(
        `${BACKEND_URL}/comment/delete/${commentId}`,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      alert("댓글이 삭제되었습니다.");
      fetchCommentList(postId);
    } catch (error) {
      if (error.response) {
        if (error.response.status === 400) {
          alert("해당 댓글을 찾을 수 없습니다.");
        } else if (error.response.status === 403) {
          alert("댓글 삭제 권한이 없습니다.");
        }
      } else {
        console.error("Error deleting comment:", error);
        alert("서버 오류가 발생했습니다.");
      }
    }
  };

  useEffect(() => {
    fetchPostDetail(postId);
    fetchCommentList(postId);
  }, []);

  return (
    <div className={poststyle.container}>
      {!detail[0] ? (
        <h1 className={display.titleFont}>LOADING</h1>
      ) : (
        <div>
          <div>
            <p
              className={`${display.nameFont} ${display.clickable}`}
              onClick={() => navigate("/postlist")}
              style={{ color: "grey" }}
            >
              {`>`} 게시판
            </p>
          </div>
          <div className={`${display.nameFont}`}>
            <div>
              <h3 style={{ margin: "10px 0px" }}>#{detail[0].postId}</h3>
              <div className={poststyle.titleContainer}>
                <h1>{detail[0].title}</h1>
                <p>{detail[0].genre}</p>
              </div>
            </div>
            <div className={poststyle.authorContainer}>
              <p>작성자: {detail[0].id}</p>
              {localStorage.getItem("userId") === detail[0].id ? (
                <p
                  className={display.clickable}
                  onClick={() => deletePost()}
                  style={{ color: "red" }}
                >
                  삭제
                </p>
              ) : null}
            </div>
          </div>
          <div>
            <img
              style={{ padding: "10px 25px" }}
              src={detail[0].movie1stPath ? detail[0].movie1stPath : noImage} // 첫 번째 포스터
              img="img"
            />
            <img
              style={{ padding: "10px 25px" }}
              src={detail[0].movie2ndPath ? detail[0].movie2ndPath : noImage} // 두 번째 포스터
              img="img"
            />
          </div>
          <div className={`${display.nameFont} ${poststyle.textContainer}`}>
            <div>
              <h3>AI STORY</h3>
              <p>
                {detail[0].aiStory
                  ? detail[0].aiStory
                  : "AI가 생성한 줄거리가 없습니다."}
              </p>
            </div>
            <div>
              <h3>COMMENT</h3>
              <p>
                {detail[0].content
                  ? detail[0].content
                  : "작성된 내용이 없습니다."}
              </p>
            </div>
            <div>
              <button
                onClick={() => handleLike()}
                className={`${display.titleFont} ${display.clickable} ${poststyle.likeButton}`}
              >
                좋아요 {detail[0].totalLikes}개
              </button>
            </div>
          </div>
          <div className={`${poststyle.commentContainer}`}>
            <div className={`${poststyle.commentInput}`}>
              <textarea
                className={`${display.nameFont}`}
                value={comment}
                onChange={handleCommentChange}
                placeholder="댓글을 입력하세요."
              />
              <button
                onClick={() => handleCommentSubmit(comment)}
                className={`${display.clickable} ${display.titleFont}`}
              >
                등록
              </button>
            </div>
            <div>
              {commentList.map((comment) => (
                <div className={`${display.nameFont} ${poststyle.comment}`}>
                  <div className={`${poststyle.authorContainer}`}>
                    <p>{comment.id}</p>
                    {localStorage.getItem("userId") === comment.id ? (
                      <p
                        className={display.clickable}
                        onClick={() => deleteComment(comment.commentId)}
                        style={{ color: "red" }}
                      >
                        삭제
                      </p>
                    ) : null}
                  </div>
                  <p>{comment.content}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Post;
