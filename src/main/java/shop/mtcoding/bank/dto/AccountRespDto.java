package shop.mtcoding.bank.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

public class AccountRespDto {

    @Setter
    @Getter
    public static class AccountDetailRespDto {
        private Long id;
        private Long number;
        private String fullName; // user.fullName
        private Long balance;

        private List<TransactionDto> transactions = new ArrayList<>();

        public AccountDetailRespDto(Account account,
                List<Transaction> transactions) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.fullName = account.getUser().getFullName();
            this.balance = account.getBalance();
            this.transactions = transactions.stream().map(TransactionDto::new).collect(Collectors.toList());
        }

        @Setter
        @Getter
        public class TransactionDto {
            private Long id;
            private Long amount;
            private Long balance;
            private String gubun;
            private String createdAt;
            private String from;
            private String to;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.amount = transaction.getAmount();
                this.gubun = transaction.getGubun().getValue(); // 입금, 출금, 이체
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());

                if (transaction.getGubun().name().equals("WITHDRAW")) {
                    this.balance = transaction.getWithdrawAccountBalance(); // 출금된 내 계좌
                    this.from = transaction.getWithdrawAccount().getNumber() + "";
                    this.to = "ATM";
                } else if (transaction.getGubun().name().equals("DEPOSIT")) {
                    this.balance = transaction.getDepositAccountBalance(); // 입금된 내 계좌
                    this.from = "ATM";
                    this.to = transaction.getDepositAccount().getNumber() + "";
                } else if (transaction.getGubun().name().equals("TRANSFER")) {
                    this.balance = transaction.getWithdrawAccountBalance(); // 출금된 내 계좌
                    this.from = transaction.getWithdrawAccount().getNumber() + "";
                    this.to = transaction.getDepositAccount().getNumber() + "";
                }
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
