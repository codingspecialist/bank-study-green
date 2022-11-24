package shop.mtcoding.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// http code = 200(get, delete, put), 201(post)
@RequiredArgsConstructor
@Getter
public class ResponseDto<T> {
    private final String msg;
    private final T data;
}
