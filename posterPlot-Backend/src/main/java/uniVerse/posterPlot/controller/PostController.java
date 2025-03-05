package uniVerse.posterPlot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PreUpdate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uniVerse.posterPlot.dto.PostListResponseDto;
import uniVerse.posterPlot.dto.PostRequestDto;
import uniVerse.posterPlot.dto.PostResponseDto;
import uniVerse.posterPlot.entity.AiStoryEntity;
import uniVerse.posterPlot.entity.Genre;
import uniVerse.posterPlot.entity.UserEntity;
import uniVerse.posterPlot.service.PostService;
import uniVerse.posterPlot.util.SecurityUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {


    private final PostService postService;

    // Ai Story 띄우기
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
            String story = postService.getAiStory(aiStoryId).getStory();
            return ResponseEntity.ok(story);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }

    // 영화 포스터 띄우기
    @Operation(
            summary = "사용자의 AI Story 기반 영화 포스터 목록 조회",
            description = "현재 로그인한 사용자의 AI Story와 관련된 영화 포스터 목록을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "영화 포스터 목록 반환 성공"),
                    @ApiResponse(responseCode = "400", description = "영화 포스터를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @GetMapping("/moviePosters")
    public ResponseEntity<List<String>> getMoviePosters(@RequestParam(name = "aiStoryId") Integer aiStoryId){
        try {
            List<String> movieList = postService.getMovieList(aiStoryId);
            return ResponseEntity.ok(movieList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 게시글 작성
    @Operation(
            summary = "게시글 작성",
            description = "유저가 새로운 게시글을 작성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 작성되었습니다."),
                    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않습니다."),
                    @ApiResponse(responseCode = "401", description = "로그인 유저가 아닙니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<String> createPost(@Valid @RequestBody PostRequestDto requestDto){
        try {
            UserEntity user = SecurityUtil.getAuthenticatedUser();
            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 유저가 아닙니다.");
            if (requestDto.getAiStoryId() == null)
                requestDto.setAiStoryId(null);
            postService.createPost(user, requestDto);
            return ResponseEntity.ok("게시글이 성공적으로 작성되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청 데이터가 유효하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }

    @Operation(
            summary = "게시물 띄우기",
            description = "게시물 목록에서 게시글 클릭시 게시물을 보여줍니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 반환 성공"),
                    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @GetMapping("/view")
    public ResponseEntity<PostResponseDto> getPost(@Valid @RequestParam Integer postId){
        try {
            PostResponseDto responseDto = postService.getPost(postId);
            return ResponseEntity.ok(responseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
            summary = "게시물 목록 띄우기",
            description = "게시물 목록을 요청된 정렬 기준에 따라 반환합니다. " +
                    "가능한 정렬 기준: 'latest' (최신순), 'oldest' (오래된순), 'likes' (좋아요순), 'top_likes' (좋아요 상위 3개)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 목록 반환 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 타입입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
            }
    )
    @GetMapping("/list")
    public ResponseEntity<List<PostListResponseDto>> getPosts(@RequestParam String type){
        List<PostListResponseDto> posts;
        try {
            switch (type) {
                case "latest":
                    posts = postService.getPostsLatest();
                    break;
                case "oldest":
                    posts = postService.getPostsOldest();
                    break;
                case "likes":
                    posts = postService.getPostsByLikes();
                    break;
                case "top_likes":
                    posts = postService.getPostsTopLikes();
                    break;
                default:
                    log.warn("잘못된 요청 타입: {}", type);
                    throw new IllegalArgumentException("잘못된 요청 타입입니다.");
            }
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // 유저별 게시글 조회
    @Operation(
            summary = "유저별 게시글 조회",
            description = "현재 로그인한 유저의 게시글 목록을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 목록 반환 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
            }
    )
    @GetMapping("/list/my")
    public ResponseEntity<List<PostListResponseDto>> getUserPosts() {
        try {
            Integer userId = SecurityUtil.getAuthenticatedUser().getUserId();
            List<PostListResponseDto> posts = postService.getPostsByUser(userId);
            return ResponseEntity.ok(posts);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 장르별 조회
    @Operation(
            summary = "장르별 게시글 조회",
            description = "입력된 장르에 해당하는 게시글 목록을 반환합니다. " +
                    "예시: 'ACTION', 'COMEDY', 'DRAMA', 'SCIFI' 등",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 목록 반환 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 장르 값입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
            }
    )
    @GetMapping("/list/{genre}")
    public ResponseEntity<List<PostListResponseDto>> getGenrePosts(@PathVariable Genre genre) {
        try {
            List<PostListResponseDto> posts = postService.getPostsByGenre(genre);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 글 삭제
    @Operation(
            summary = "게시물 삭제",
            description = "게시물을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 삭제되었습니다."),
                    @ApiResponse(responseCode = "400", description = "해당 게시글을 찾을 수 없거나 삭제 권한이 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@Valid @PathVariable Integer postId){
        try {
            Integer userId = SecurityUtil.getAuthenticatedUser().getUserId();
            postService.deletePost(postId, userId);
            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }

    // 좋아요 기능
    @Operation(
            summary = "게시물 좋아요",
            description = "게시글에 좋아요를 누릅니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "좋아요 성공"),
                    @ApiResponse(responseCode = "400", description = "이미 좋아요를 누르셨습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @PostMapping("/like")
    public ResponseEntity<String> likePost(@RequestParam(name = "postId") Integer postId){
        try {
            Integer userId = SecurityUtil.getAuthenticatedUser().getUserId();
            postService.likePost(postId, userId);
            return ResponseEntity.ok("좋아요 성공");
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }
}
