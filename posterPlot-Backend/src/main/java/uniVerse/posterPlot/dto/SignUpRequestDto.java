package uniVerse.posterPlot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(example = "user123")
    private String id;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상 15자 이하여야 합니다.")
    @Schema(example = "password123")
    private String password;

    @Email(message = "유효한 이메일 형식이 아닙니다")
    @NotEmpty(message = "이메일을 입력해 주세요")
    @Schema(example = "user123@gmail.com")
    private String email;

    public SignUpRequestDto(String id, String password,String email) {
        this.id = id;
        this.password = password;
        this.email = email;
    }

}
