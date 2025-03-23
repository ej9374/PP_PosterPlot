package uniVerse.posterPlot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostListHomeResponseDto {
    @Schema(example = "123")
    private Integer postId;

    @Schema(example = "title")
    private String title;

    @Schema(example = "user123")
    private String id;

    private String movie1stPath;

    private String movie2ndPath;

    public PostListHomeResponseDto(Integer postId, String title, String id, String movie1stPath, String movie2ndPath) {
        this.postId = postId;
        this.title = title;
        this.id = id;
        this.movie1stPath = movie1stPath;
        this.movie2ndPath = movie2ndPath;
    }
}
