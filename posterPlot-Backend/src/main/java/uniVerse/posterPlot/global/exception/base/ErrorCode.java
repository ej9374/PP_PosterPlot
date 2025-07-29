package uniVerse.posterPlot.global.exception.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),

    JWT_SECRET_NOT_FOUND(HttpStatus.BAD_REQUEST, "jwt 비밀키가 존재하지 않습니다.");


    private final HttpStatus status;
    private final String message;
}

