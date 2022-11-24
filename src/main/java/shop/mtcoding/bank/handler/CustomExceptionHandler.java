package shop.mtcoding.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import shop.mtcoding.bank.config.exception.CustomApiException;
import shop.mtcoding.bank.dto.ResponseDto;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e) {
        System.out.println("에러의 제어권을 잡음");
        return new ResponseEntity<>(new ResponseDto<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
