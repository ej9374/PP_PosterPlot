package uniVerse.posterPlot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uniVerse.posterPlot.entity.Genre;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {

    private Integer postId;
    private String id;
    private String title;
    private String content;
    private Integer totalLikes;
    private Genre genre;
    private String aiStory;
    private String movie1stPath;
    private String movie2ndPath;

    public PostResponseDto(Integer postId, String id, String title, String content, Integer totalLikes, Genre genre, String aiStory, String movie1stPath, String movie2ndPath) {
        this.postId = postId;
        this.id = id;
        this.title = title;
        this.content = content;
        this.totalLikes = totalLikes;
        this.genre = genre;
        this.aiStory = aiStory;
        this.movie1stPath = movie1stPath;
        this.movie2ndPath = movie2ndPath;
    }
}
