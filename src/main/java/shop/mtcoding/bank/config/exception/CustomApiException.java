package shop.mtcoding.bank.config.exception;

import lombok.Getter;

@Getter
public class CustomApiException extends RuntimeException {

    private final int httpStatusCode;

    public CustomApiException(String msg, int httpStatusCode) {
        super(msg);
        this.httpStatusCode = httpStatusCode;
    }
}
