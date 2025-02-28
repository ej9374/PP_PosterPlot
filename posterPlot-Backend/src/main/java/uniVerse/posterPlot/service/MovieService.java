package uniVerse.posterPlot.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uniVerse.posterPlot.entity.MovieListEntity;
import uniVerse.posterPlot.repository.MovieListRepository;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Blob;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class MovieService {

    private final MovieListRepository movieListRepository;
//    private final Storage storage = StorageOptions.getDefaultInstance().getService();;
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
    public void uploadMovieImage(List<MultipartFile> files) {
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

        saveMovieImage(movie1stPath, movie2ndPath);
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
    public void saveMovieImage(String movie1stPath, String movie2ndPath){
        MovieListEntity movieListEntity = new MovieListEntity(movie1stPath, movie2ndPath);
        movieListRepository.save(movieListEntity);
        log.info("영화 이미지 저장 완료");

    }


    //ai로 영화 이미지 넘겨주기

    // ai 이야기 string 전달받기
}
