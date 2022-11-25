package shop.mtcoding.bank.util;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class CustomDateUtilTest {

    @Test
    public void toStringFormat_test() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();

        // when
        String result = CustomDateUtil.toStringFormat(localDateTime);
        System.out.println(result);
    }
}
