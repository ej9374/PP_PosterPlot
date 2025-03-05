package uniVerse.posterPlot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendToFlaskRequestDto {
    private Integer movieListId;

    @JsonProperty("image_urls")
    private List<String> imageUrls;

    public SendToFlaskRequestDto(Integer movieListId, List<String> imageUrls) {
        this.movieListId = movieListId;
        this.imageUrls = imageUrls;
    }
}
