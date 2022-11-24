package shop.mtcoding.bank.config.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomApiException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }
}
