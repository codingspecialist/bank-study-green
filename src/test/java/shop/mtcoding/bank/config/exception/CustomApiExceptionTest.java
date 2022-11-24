package shop.mtcoding.bank.config.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class CustomApiExceptionTest {

    @Test
    public void customApi_test() throws Exception {
        // given
        String msg = "해당 id가 없습니다.";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        // when
        CustomApiException ex = new CustomApiException(msg, httpStatus);
        System.out.println(ex.getHttpStatus());
        System.out.println(ex.getHttpStatus().value());
        System.out.println(ex.getMessage());

        // then
        assertThat(ex.getHttpStatus().value()).isEqualTo(400);
        assertThat(ex.getMessage()).isEqualTo("해당 id가 없습니다.");
    }
}
