package uniVerse.posterPlot.global.exception.base;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ErrorCode errorCode;

    public CustomException(ErrorCode code){
        super(code.getMessage());
        this.errorCode = code;
    }
}

