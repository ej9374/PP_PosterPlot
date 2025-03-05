package uniVerse.posterPlot.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uniVerse.posterPlot.dto.ReceiveFlaskResponseDto;
import uniVerse.posterPlot.dto.SendToFlaskRequestDto;
import uniVerse.posterPlot.entity.AiStoryEntity;
import uniVerse.posterPlot.entity.MovieListEntity;
import uniVerse.posterPlot.entity.UserEntity;
import uniVerse.posterPlot.repository.AiStoryRepository;
import uniVerse.posterPlot.repository.MovieListRepository;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Blob;
import uniVerse.posterPlot.repository.UserRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class MovieService {

    private final MovieListRepository movieListRepository;
    private final AiStoryRepository aiStoryRepository;
    private final UserRepository userRepository;
    private final String bucketName = "posterplot-movie-images";  // GCP 버킷 이름
    private Storage storage;

    {
        try {
            storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(
                            new FileInputStream("src/main/resources/gcp-keys/posterplot-key.json") // 🔥 예외 처리 추가
                    ))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException("GCP 인증 키 파일을 로드할 수 없습니다: " + e.getMessage(), e);
        }
    }

    //영화 포스터 유저가 업로드 하는 메서드
    @Transactional
    public Integer uploadMovieImage(UserEntity user, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        log.info("업로드된 파일 개수: {}", files.size()); // 파일 개수 확인
        if (files.size() < 2) {
            throw new IllegalArgumentException("최소 2개의 이미지를 업로드해야 합니다.");
        }

        String movie1stPath = uploadToGCP(files.get(0));
        String movie2ndPath = uploadToGCP(files.get(1));

        log.info("첫 번째 파일 업로드 경로: {}", movie1stPath);
        log.info("두 번째 파일 업로드 경로: {}", movie2ndPath);

        MovieListEntity movieList = saveMovieImage(user, movie1stPath, movie2ndPath);

        return movieList.getMovieListId();
    }


    @Transactional
    public String uploadToGCP(MultipartFile file){
        try {String originalFilename = file.getOriginalFilename();
            log.info("업로드할 파일 이름: {}", originalFilename);

            String uuid = UUID.randomUUID().toString();
            String newFilename = uuid+"_"+originalFilename;

            BlobId blobId = BlobId.of(bucketName, newFilename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            Blob blob = storage.create(blobInfo, file.getBytes());

            String uploadedUrl = blob.getMediaLink();
            log.info("파일 업로드 성공: {}", uploadedUrl);
            // GCP에 업로드된 이미지 URL 반환
            return uploadedUrl;
        } catch(IOException e){
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }
    }

    @Transactional
    public MovieListEntity saveMovieImage(UserEntity user, String movie1stPath, String movie2ndPath){
        MovieListEntity movieList = new MovieListEntity(user, movie1stPath, movie2ndPath);
        movieListRepository.save(movieList);
        log.info("영화 이미지 저장 완료");
        return movieList;
    }


    @Transactional
    public Integer sendMovieListToFlask(Integer movieListId) {

        WebClient webClient = WebClient.builder()
                .baseUrl("http://127.0.0.1:5000") //Flask API URL
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("sendMovieListToFlask 호출됨. movieListId={}", movieListId);

        MovieListEntity movieList = movieListRepository.findByMovieListId(movieListId);
        if (movieList == null) {
            log.error("해당 movieListId={}에 대한 데이터가 없습니다.", movieListId);
            throw new RuntimeException("해당 movieListId에 대한 데이터가 없습니다.");
        }

        SendToFlaskRequestDto flaskRequestDto = new SendToFlaskRequestDto(
                movieListId,
                List.of(movieList.getMovie1stPath(),movieList.getMovie2ndPath())
        );

        log.info("Flask로 전송할 JSON 데이터: {}", flaskRequestDto);

        try {
            ReceiveFlaskResponseDto flaskResponseDto
                    = webClient.post()
                    .uri("generate_story")
                    .bodyValue(flaskRequestDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        log.error("❌ Flask API 호출 실패: 상태 코드 {}, 응답: {}", response.statusCode(), response.bodyToMono(String.class).block());
                        return Mono.error(new RuntimeException("Flask API 오류 발생: " + response.bodyToMono(String.class).block()));
                    })
                    .bodyToMono(ReceiveFlaskResponseDto.class)
                    .block();

            if (flaskResponseDto == null){
                log.error("API 응답이 옳지 않습니다.: {}", flaskResponseDto);
                throw new RuntimeException("Flask API 응답이 올바르지 않습니다.");
            }

            Integer responseMovieListId = flaskResponseDto.getMovieListId();
            String story = flaskResponseDto.getGeneratedStory();
            log.info("API 응답 수신 완료: movieListId={}, story={}", responseMovieListId, story);

            MovieListEntity findMovieList = movieListRepository.findByMovieListId(movieListId);
            if (findMovieList == null) {
                log.error("movieListId={}에 해당하는 데이터를 찾을 수 없습니다.", movieListId);
                throw new RuntimeException("Flask API 응답 데이터가 DB에 존재하지 않습니다.");
            }
            return saveAiStory(findMovieList, story);
        } catch (Exception e) {
            log.error("🚨 Flask API 호출 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Flask API 호출 실패: " + e.getMessage(), e);
        }
    }

    public Integer saveAiStory(MovieListEntity movieList, String story) {
        AiStoryEntity aiStory = new AiStoryEntity(story, movieList);
        aiStoryRepository.save(aiStory);
        log.info("AI 스토리 저장 완료: aiStoryId={}", aiStory.getAiStoryId());
        return aiStory.getAiStoryId();
    }

    @Transactional
    public AiStoryEntity getAiStory(Integer aiStoryId) {
        AiStoryEntity aiStory = aiStoryRepository.findAiStoryById(aiStoryId);

        if (aiStory == null){
            throw new RuntimeException("Ai Story를 찾을 수 없습니다.");
        }
        return aiStory;
    }
}
