package uniVerse.posterPlot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReceiveFlaskResponseDto {

    private Integer movieListId;

    @JsonProperty("generated_story")
    private String generatedStory;
}
