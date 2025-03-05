package uniVerse.posterPlot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentListResponseDto {

    private Integer commentId;
    private Integer userId;
    private String content;
}
