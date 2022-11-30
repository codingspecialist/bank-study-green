package shop.mtcoding.bank.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

public class AccountRespDto {

    @Setter
    @Getter
    public static class AccountListRespDtoV3 {
        private UserDto user;
        private List<AccountDto> accounts;

        public AccountListRespDtoV3(User user) {
            this.user = new UserDto(user);
            this.accounts = user.getAccounts().stream().map((account) -> new AccountDto(account))
                    .collect(Collectors.toList());
        }

        @Setter
        @Getter
        public class UserDto {
            private Long id;
            private String fullName;

            public UserDto(User user) {
                this.id = user.getId();
                this.fullName = user.getFullName();
            }
        }

        @Setter
        @Getter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
                account.getUser().getUsername(); // Lazy 로딩
            }
        }
    }

    @Setter
    @Getter
    public static class AccountListRespDto {
        private UserDto user;
        private List<AccountDto> accounts;

        public AccountListRespDto(User user) {
            this.user = new UserDto(user);
        }

        public AccountListRespDto(List<Account> accounts) {
            this.user = new UserDto(accounts.get(0).getUser());
            this.accounts = accounts.stream().map((account) -> new AccountDto(account)).collect(Collectors.toList());
        }

        public AccountListRespDto(User user, List<Account> accounts) {
            this.user = new UserDto(user);
            this.accounts = accounts.stream().map((account) -> new AccountDto(account)).collect(Collectors.toList());
        }

        @Setter
        @Getter
        public class UserDto {
            private Long id;
            private String fullName;

            public UserDto(User user) {
                this.id = user.getId();
                this.fullName = user.getFullName();
            }
        }

        @Setter
        @Getter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }

    @Setter
    @Getter
    public static class AccountSaveRespDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }
}
