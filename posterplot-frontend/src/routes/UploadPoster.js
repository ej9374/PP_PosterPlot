import { React, useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import display from "../styles/Display.module.css";
import createstyle from "../styles/CreateStyle.module.css";

import noImage from "../icons/no-image-movie.jpg";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

// AI Text 화면 출력
const TypingText = ({ text, speed }) => {
  const [displayedText, setDisplayedText] = useState("");

  useEffect(() => {
    setDisplayedText(""); // 초기화
    let isCancelled = false;

    const typeText = (i) => {
      if (isCancelled) return;
      if (i < text.length) {
        setDisplayedText((prev) => prev + text[i]);
        setTimeout(() => typeText(i + 1), speed);
      }
    };

    typeText(0); // 재귀 호출 시작

    return () => {
      isCancelled = true; // 컴포넌트가 언마운트되면 실행 중지
    };
  }, [text, speed]);

  return (
    <p style={{ lineHeight: "2", textAlign: "justify" }}>{displayedText}</p>
  );
};

const processMoviePoster = async (formData) => {
  let movieListId = null;
  let aiStoryId = null;
  let aiStoryText = null;

  // upload poster -> fetch movieListid
  try {
    const uploadResponse = await axios.post(
      `${BACKEND_URL}/movie/upload`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
    const match = uploadResponse.data.match(/movieListId\s*=\s*(\d+)/);
    movieListId = match ? parseInt(match[1], 10) : null;
    if (!movieListId) throw new Error("movieListId를 추출할 수 없습니다.");

    console.log("movieListId:", movieListId);
  } catch (error) {
    console.error("Error uploading poster:", error);
    return null; // 이후 요청 중단
  }

  // upload movieListId -> fetch AI Story id
  try {
    const storyResponse = await axios.post(
      `${BACKEND_URL}/movie/getStory`,
      new URLSearchParams({ movieListId }),
      {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
    aiStoryId = storyResponse.data;
    if (!aiStoryId) throw new Error("aiStoryId를 가져올 수 없습니다.");

    console.log("aiStoryId:", storyResponse.data);
  } catch (error) {
    console.error("Error fetching AI Story id:", error);
    return null; // 이후 요청 중단
  }

  // upload aiStoryId -> fetch AI Story Text
  try {
    const textResponse = await axios.get(`${BACKEND_URL}/movie/aiStory`, {
      params: { aiStoryId },
    });

    console.log("AI Story Text:", textResponse.data);
    return textResponse.data;
  } catch (error) {
    console.error("Error fetching AI Story:", error);
    return null;
  }
};

function UploadPoster() {
  const [files, setFiles] = useState([]);
  const [posterUrls, setPosterUrls] = useState("");

  const [AItext, setAItext] = useState("");

  const navigate = useNavigate();
  const bottomRef = useRef(null);

  const onClickPosterUpload = () => {
    if (files.length >= 2) {
      alert("이미지는 2개까지 올릴 수 있습니다.");
    }
    return;
  };

  const onChangePosterUpload = (event) => {
    const selectedFiles = event.target.files;

    if (selectedFiles && selectedFiles.length > 0) {
      // files에 선택한 이미지 저장
      const updatedFiles = [...files, ...Array.from(selectedFiles)];
      if (updatedFiles.length > 2) {
        alert("이미지는 2개까지 올릴 수 있습니다.");
        return;
      }
      setFiles(updatedFiles);

      // posterUrl에 선택한 이미지 url 저장
      for (let i = 0; i < selectedFiles.length; i++) {
        const reader = new FileReader();
        reader.readAsDataURL(selectedFiles[i]);
        reader.onloadend = () => {
          setPosterUrls((prevUrls) => {
            const newUrls = [...prevUrls, reader.result]; // base64 인코딩된 이미지 저장
            return newUrls;
          }); // base64 인코딩된 이미지 저장
        };
      }
    }
  };

  const deleteUploadedPoster = () => {
    setFiles([]);
    setPosterUrls("");
  };

  const handlePostCreation = async () => {
    if (files.length !== 2) {
      alert("포스터는 2개 올릴 수 있습니다.");
      return;
    }

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append("files", files[i]);
    }

    processMoviePoster(formData);

    // 밑에 지우기!
    /*
    setAItext(`2029년, 인공지능 기술이 발달한 미래. 인간의 기억을 디지털화하여 저장하고 되돌려볼 수 있는 신경 데이터 회사 "메모리아"가 세계적인 혁신 기업으로 떠오른다. 사람들은 소중한 기억을 보관하고, 때로는 불필요한 기억을 삭제하는 등 자유롭게 조작하며 살아간다. 
      주인공 윤지후(35세, 신경과학자)는 메모리아에서 연구원으로 일하며, 기억을 분석하고 복원하는 일을 담당한다. 어느 날, 그는 한 노인의 기억을 복원하는 프로젝트를 맡게 된다. 노인은 자신이 잃어버린 30년 전의 중요한 기억을 되찾고 싶어 하지만, 데이터를 복원하는 과정에서 이상한 점이 발견된다. 노인의 기억 속에서 '존재하지 않는 장소'와 '기록되지 않은 인물'이 나타난 것이다.
      지후는 이를 단순한 오류라고 생각했지만, 점점 기억 속 단서들을 분석할수록 어떤 거대한 음모가 숨겨져 있음을 직감한다. 조사 끝에 그는 한 가지 충격적인 사실을 알게 된다. 메모리아는 단순한 기억 저장소가 아니라, 특정 기억을 조작하고 삭제할 수 있는 시스템을 운영하고 있었던 것. 그리고 그 기술은 특정 권력층에게 이용되어 과거를 지우고, 역사를 바꿔버리는 도구로 사용되고 있었다.
      지후는 메모리아의 어두운 비밀을 폭로하기 위해 내부 데이터를 빼내려 하지만, 이미 그의 존재는 회사의 감시망에 포착된다. 연구소에서 도망치던 그는 동료이자 해커인 이나영(32세)과 함께 기억을 복구할 수 있는 비밀 서버를 찾아 나선다. 하지만 그 과정에서 자신도 알지 못했던 사실을 깨닫는다.
      그는 어릴 때부터 특정한 기억이 반복적으로 떠올랐지만, 항상 꿈이라고 생각해 왔다. 하지만 그것이 단순한 꿈이 아니라는 걸 알게 된다. 그는 이미 한 번 메모리아에 의해 기억이 조작된 피해자였으며, 그의 부모 역시 과거의 음모를 파헤치다 실종된 것이었다.
      진실을 밝히기 위해 마지막으로 메모리아 본사에 잠입한 지후와 나영. 둘은 비밀 서버를 해킹하여 모든 조작된 기억을 원래대로 되돌리는 데 성공하지만, 그 과정에서 심각한 부작용이 발생한다. 수많은 사람이 잊고 있던 기억을 한순간에 되찾으며 사회가 혼란에 빠지고, 메모리아의 비밀이 온 세상에 공개된다.
      결국 회사는 붕괴하고 관련자들은 법의 심판을 받지만, 지후는 알 수 없는 이유로 기억이 점점 사라지기 시작한다. 그는 모든 것을 되돌렸지만, 자신이 누구였는지조차 잊혀 가고 있었다.
      마지막 장면, 한적한 해변에서 낯선 사람처럼 앉아 있는 지후. 파도 소리를 들으며 그는 희미하게 중얼거린다.
      "나는… 누구였지?"`);*/
  };

  useEffect(() => {
    if (AItext) {
      bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [AItext]);

  const handleNavigate = () => {
    navigate("/createpost", {
      state: { uploadedPoster: posterUrls, AItext: AItext },
    });
  };

  return (
    <div>
      <div className={createstyle.container}>
        <div align="center">
          <div>
            <h1 className={display.titleFont} style={{ margin: "5px" }}>
              포스터 등록하기
            </h1>
            <p className={display.nameFont}>
              포스터를 등록하고 AI 줄거리를 생성해보세요!
            </p>
          </div>
          <div>
            <img
              src={posterUrls[0] ? posterUrls[0] : noImage} // 첫 번째 포스터
              img="img"
            />
            <img
              src={posterUrls[1] ? posterUrls[1] : noImage} // 두 번째 포스터
              img="img"
            />
          </div>
          <p className={display.nameFont}>※ 2장의 포스터를 선택해주세요!</p>
          <div>
            {posterUrls && (
              <button
                onClick={deleteUploadedPoster}
                className={createstyle.initializeButton}
              >
                초기화
              </button>
            )}
            <div>
              <input
                type="file"
                accept="image/*"
                multiple={true}
                onChange={onChangePosterUpload}
                disabled={files.length >= 2}
                id="file-input"
                style={{ display: "none" }} // 기본 input 숨김
              />
              <label
                onClick={onClickPosterUpload}
                htmlFor="file-input"
                className={`${display.clickable} ${createstyle.fileButton}`}
              >
                파일 선택
              </label>
            </div>
            <button
              disabled={posterUrls.length !== 2}
              onClick={handlePostCreation}
              className={`${display.titlefont} ${createstyle.AIButton}`}
            >
              AI 줄거리 생성하기
            </button>
            {AItext && (
              <div>
                <div className={`${display.nameFont} ${createstyle.AItext}`}>
                  <h3 style={{ color: "#aaaaaa" }}>
                    포스터를 기반으로 AI가 분석한 줄거리입니다!
                  </h3>
                  <TypingText text={AItext} speed={20} />
                </div>
                <button
                  className={createstyle.AIButton}
                  onClick={handleNavigate}
                  ref={bottomRef}
                >
                  시나리오 작성하기
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default UploadPoster;
