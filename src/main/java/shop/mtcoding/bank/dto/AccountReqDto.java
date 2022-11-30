package shop.mtcoding.bank.dto;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

public class AccountReqDto {

    @Setter
    @Getter
    public static class AccountDeleteReqDto {
        private String password;
    }

    @Setter
    @Getter
    public static class AccountSaveReqDto {
        private Long number;
        private String password;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .isActive(true)
                    .build();
        }
    }
}
