package uniVerse.posterPlot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uniVerse.posterPlot.entity.UserEntity;
import uniVerse.posterPlot.service.MovieService;
import uniVerse.posterPlot.util.SecurityUtil;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movie")

public class MovieController {

    private final MovieService movieService;

    @Operation(
            summary = "영화 포스터 업로드",
            description = "사용자가 2개의 영화 포스터 이미지를 업로드하면, 이미지가 서버에 저장됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이미지가 성공적으로 업로드되었습니다."),
                    @ApiResponse(responseCode = "400", description = "파일은 반드시 2개를 업로드 해야 합니다."),
                    @ApiResponse(responseCode = "401", description = "로그인 유저가 아닙니다.")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("files") List<MultipartFile> files) {
        UserEntity user = SecurityUtil.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 유저가 아닙니다.");

        if (files == null || files.size() != 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일은 반드시 2개를 업로드 해야 합니다.");
        }

        Integer movieListId = movieService.uploadMovieImage(user, files);
        return ResponseEntity.ok("이미지가 성공적으로 업로드 되었습니다. movieListId = " + movieListId);
    }

    @Operation(
            summary = "영화 줄거리 생성 요청",
            description = "Flask 서버로 movieListId를 전달하여 AI 기반 영화 줄거리를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "영화 줄거리가 성공적으로 생성되었습니다. aiStoryId 반환"),
                    @ApiResponse(responseCode = "400", description = "올바르지 않은 요청입니다. movieListId가 유효하지 않거나 데이터가 없습니다."),
                    @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
                    @ApiResponse(responseCode = "500", description = "Flask API 호출 중 서버 오류가 발생했습니다.")
            }
    )
    @PostMapping("/getStory")
    public ResponseEntity<?> sendToFlask(
            @Parameter(name = "movieListId", description = "업로드된 영화 포스터에 해당하는 ID", required = true)
            @RequestParam("movieListId") Integer movieListId) {
        UserEntity user = SecurityUtil.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 유저가 아닙니다.");

        try {
            Integer aiStoryId = movieService.sendMovieListToFlask(movieListId);
            return ResponseEntity.ok(aiStoryId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Flask API 호출 중 오류가 발생했습니다.");
        }
    }

    @Operation(
            summary = "사용자의 AI Story 조회",
            description = "현재 로그인한 사용자의 AI Story를 조회하여 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "AI Story 반환 성공"),
                    @ApiResponse(responseCode = "400", description = "Ai Story를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @GetMapping("/aiStory")
    public ResponseEntity<String> getAiStory(@RequestParam(name = "aiStoryId") Integer aiStoryId){
        try {
            String story = movieService.getAiStory(aiStoryId).getStory();
            return ResponseEntity.ok(story);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }
}
