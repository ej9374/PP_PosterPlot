package uniVerse.posterPlot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailCheckDto {

    @Email
    @NotEmpty(message = "이메일을 입력해 주세요")
    @Schema(example = "user123@gmail.com")
    private String email;

    @NotEmpty(message = "인증 번호를 입력해 주세요")
    @Schema(example = "123456")
    private String authNum;

}