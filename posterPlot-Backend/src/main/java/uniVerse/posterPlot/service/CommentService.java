package uniVerse.posterPlot.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uniVerse.posterPlot.dto.CommentListResponseDto;
import uniVerse.posterPlot.entity.CommentEntity;
import uniVerse.posterPlot.entity.PostEntity;
import uniVerse.posterPlot.entity.UserEntity;
import uniVerse.posterPlot.repository.CommentRepository;
import uniVerse.posterPlot.repository.PostRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 댓글 등록
    @Transactional
    public void createComment(UserEntity user, Integer postId, String content) {

        Integer userId = user.getUserId();

        CommentEntity comment = new CommentEntity(user, postRepository.findByPostId(postId), content);
        commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        CommentEntity comment = commentRepository.findByCommentId(commentId);

        if (comment == null) {
            throw new RuntimeException("해당 댓글을 찾을 수 없습니다.");
        }

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(commentId);
    }



    // 댓글 리스트 보여주기
    public List<CommentListResponseDto> getComments(Integer postId) {
        PostEntity post = postRepository.findByPostId(postId);
        if (post == null) {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다. postId: " + postId);
        }
        List<Integer> commentIds = commentRepository.findAllByPostId(postId);
        if (commentIds.isEmpty())
            return Collections.emptyList();

        List<CommentListResponseDto> responseList = new ArrayList<>();
        for (int i = 0; i < commentIds.size(); i++) {
            CommentEntity comment = commentRepository.findByCommentId(commentIds.get(i));
            responseList.add(new CommentListResponseDto(comment.getCommentId(), comment.getUser().getUserId(), comment.getContent()));
        }
        return responseList;
    }
}
