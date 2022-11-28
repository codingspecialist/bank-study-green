package shop.mtcoding.bank.config.jwt;

import org.junit.jupiter.api.Test;

import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.config.enums.UserEnum;
import shop.mtcoding.bank.domain.user.User;

public class JwtProcessTest {

    @Test
    public void create_test() {
        // given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String token = JwtProcess.create(loginUser);
        System.out.println(token);
    }

}
