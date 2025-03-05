package uniVerse.posterPlot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uniVerse.posterPlot.dto.CommentListResponseDto;
import uniVerse.posterPlot.entity.UserEntity;
import uniVerse.posterPlot.service.CommentService;
import uniVerse.posterPlot.util.SecurityUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
@Tag(name = "CommentController", description = "CommentController API 목록")
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @Operation(
            summary = "댓글 작성",
            description = "로그인된 사용자가 특정 게시글에 댓글을 작성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 작성되었습니다."),
                    @ApiResponse(responseCode = "401", description = "로그인하지 않은 사용자입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<String> createComment(@RequestParam String content, @RequestParam Integer postId){
        try{
            UserEntity user = SecurityUtil.getAuthenticatedUser();
            if (user == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 유저가 아닙니다.");
            }
            commentService.createComment(user, postId, content);
            return ResponseEntity.ok("댓글이 성공적으로 작성되었습니다.");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }

    //댓글 삭제
    @Operation(
            summary = "댓글 삭제",
            description = "로그인된 사용자가 자신의 댓글을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 삭제되었습니다."),
                    @ApiResponse(responseCode = "400", description = "해당 댓글을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "댓글 삭제 권한이 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Integer commentId) {
        try {
            Integer userId = SecurityUtil.getAuthenticatedUser().getUserId();
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }


    //댓글 띄우기
    @Operation(
            summary = "댓글 목록 조회",
            description = "특정 게시글에 대한 모든 댓글을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 목록을 반환합니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @GetMapping("/list/{postId}")
    public ResponseEntity<List<CommentListResponseDto>> getCommentList(@PathVariable Integer postId){
        try{
            List<CommentListResponseDto> comments = commentService.getComments(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
