package shop.mtcoding.bank.config.exception;

import java.util.Map;

import lombok.Getter;

@Getter
public class CustomValidationApiException extends RuntimeException {

    private final int httpStatusCode = 400;
    private Map<String, String> errorMap;

    public CustomValidationApiException(Map<String, String> errorMap) {
        super("유효성 검사 실패");
        this.errorMap = errorMap;
    }
}
