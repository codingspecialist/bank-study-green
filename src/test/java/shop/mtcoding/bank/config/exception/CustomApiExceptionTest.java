package shop.mtcoding.bank.config.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CustomApiExceptionTest {

    @Test
    public void customApi_test() throws Exception {
        // given
        String msg = "해당 id가 없습니다.";
        int httpStatusCode = 400;

        // when
        CustomApiException ex = new CustomApiException(msg, httpStatusCode);
        System.out.println(ex.getHttpStatusCode());
        System.out.println(ex.getMessage());

        // then
        assertThat(ex.getHttpStatusCode()).isEqualTo(400);
        assertThat(ex.getMessage()).isEqualTo("해당 id가 없습니다.");
    }
}
