package uniVerse.posterPlot.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uniVerse.posterPlot.dto.EmailCheckDto;
import uniVerse.posterPlot.dto.LoginRequestDto;
import uniVerse.posterPlot.dto.SignUpRequestDto;
import uniVerse.posterPlot.service.AuthService;
import uniVerse.posterPlot.service.MailSendService;
import uniVerse.posterPlot.util.JwtProvider;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "AuthController", description = "AuthController API 목록")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final MailSendService mailService;
    private final JwtProvider jwtProvider;

    //아이디 중복 확인
    @Operation(
            summary = "아이디 중복 확인",
            description = "아이디가 이미 존재하는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 아이디입니다."),
                    @ApiResponse(responseCode = "409", description = "아이디가 이미 존재합니다.")
            }
    )
    @GetMapping("/checkId")
    public ResponseEntity<String> checkId(@Parameter(name = "id", description = "확인할 아이디", example = "user123") @RequestParam("id") String id) {
        if (authService.isExist(id)){
            log.info("Id is exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("아이디가 이미 존재합니다.");
        } else {
            log.info("Id is not exist");
            return ResponseEntity.ok("사용 가능한 아이디입니다.");
        }
    }

    //이메일 인증 보내기
    @Operation(
            summary = "이메일 인증 보내기",
            description = "이메일로 인증 번호를 발송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이메일로 인증 번호가 전송되었습니다."),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @PostMapping("/mailSend")
    public ResponseEntity<String> mailSend(@Parameter(name = "email", description = "이메일", example = "user123@gmail.com") @RequestParam @Valid @Email String email) {
        try {
            log.info("이메일 인증 이메일: {}", email);
            String verificationCode = mailService.joinEmail(email);
            return ResponseEntity.ok("인증 번호가 " + email + "로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("이메일 인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }

    //이메일 인증 확인
    @Operation(
            summary = "이메일 인증 확인",
            description = "인증 번호가 일치하는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 번호가 일치합니다."),
                    @ApiResponse(responseCode = "400", description = "인증 번호가 일치하지 않습니다.")
            }
    )
    @PostMapping("/mailAuthCheck")
    public ResponseEntity<String> AuthCheck(@RequestBody @Valid EmailCheckDto emailCheckDto) {
        if (mailService.CheckAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum())) {
            log.info("AuthNum is correct");
            return ResponseEntity.ok("인증 번호가 일치합니다.");
        } else {
            log.info("AuthNum is not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 번호가 일치하지 않습니다.");
        }
    }

    //비밀번호 확인
    @Operation(
            summary = "비밀번호 확인",
            description = "비밀번호와 비밀번호 확인이 일치하는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 비밀번호입니다."),
                    @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다.")
            }
    )
    @GetMapping("/checkPassword")
    public ResponseEntity<String> checkPassword(
            @Parameter(name = "password", description = "비밀번호", example = "password123") @RequestParam("password") String password
            , @Parameter(name = "confirmPassword", description = "비밀번호 확인", example = "password123") @RequestParam("confirmPassword") String confirmPassword) {
        if (!authService.isCorrectPassword(password, confirmPassword)){
            log.info("password is not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 일치하지 않습니다.");
        } else {
            log.info("password is correct");
            return ResponseEntity.ok("사용 가능한 비밀번호입니다.");
        }
    }

    //회원가입
    @Operation(
            summary = "회원가입",
            description = "새로운 회원을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.")
            }
    )
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        try {
            authService.signUp(signUpRequestDto);
            log.info("signUp is success");
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (Exception e) {
            log.error("회원가입 중 서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //로그인
    @Operation(
            summary = "로그인",
            description = "사용자 로그인을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인이 성공하여 jwt 토큰을 출력합니다."),
                    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 옳지 않습니다.")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        try {
            authService.login(loginRequestDto);

            // Jwt 생성
            String jwt = jwtProvider.create(loginRequestDto.getId());

            log.info("login is success: {}", loginRequestDto.getId());
            return ResponseEntity.ok(jwt);
        } catch (IllegalArgumentException e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
