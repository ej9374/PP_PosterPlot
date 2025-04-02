package uniVerse.posterPlot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CommentListResponseDto {

    private Integer commentId;
    private Integer userId;
    private String id;
    private String content;

    public CommentListResponseDto(Integer commentId, Integer userId, String id, String content) {
        this.commentId = commentId;
        this.userId = userId;
        this.id = id;
        this.content = content;
    }
}
