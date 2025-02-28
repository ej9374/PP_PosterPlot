package uniVerse.posterPlot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @Schema(example = "user123")
    private String id;

    @Schema(example = "password123")
    private String password;

    public LoginRequestDto(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
