package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.mtcoding.bank.config.enums.UserEnum;
import shop.mtcoding.bank.domain.user.User;

public class DummyEntity {

    protected User newUser(String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        User user = User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .role(username.equals("admin") ? UserEnum.ADMIN : UserEnum.CUSTOMER)
                .build();
        return user;
    }
}
