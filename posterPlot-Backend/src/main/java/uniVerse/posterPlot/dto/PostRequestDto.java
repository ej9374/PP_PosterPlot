package uniVerse.posterPlot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uniVerse.posterPlot.entity.Genre;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Schema(example = "시나리오 제목")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Schema(example = "시나리오 이야기")
    private String content;

    @NotNull(message = "장르를 선택해주세요.")
    @Schema(example = "ACTION", allowableValues = {"ACTION", "CRIME", "ROMANCE", "SCI_FI", "COMEDY", "SPORTS", "FANTASY", "MUSIC", "MUSICAL", "WAR", "HORROR", "THRILLER"})
    private Genre genre;

    @Nullable
    @Schema(example = "1", nullable = true, description = "AI 스토리 ID (없을 경우 null 가능)")
    private Integer aiStoryId;
}
