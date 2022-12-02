package shop.mtcoding.bank.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.config.enums.TransactionEnum;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;

public class TransactionReqDto {

    @Setter
    @Getter
    public static class TransferReqDto {
        private Long amount;
        private String password;
        private String gubun;

        public Transaction toEntity(Account withdrawAccount, Account depositAccount) {
            Transaction transaction = Transaction.builder()
                    .withdrawAccount(withdrawAccount)
                    .depositAccount(depositAccount)
                    .withdrawAccountBalance(withdrawAccount.getBalance())
                    .depositAccountBalance(depositAccount.getBalance())
                    .amount(amount)
                    .gubun(TransactionEnum.valueOf(gubun))
                    .build();
            return transaction;
        }
    }

    @Setter
    @Getter
    public static class WithdrawReqDto {
        @NotEmpty
        private Long amount;
        @NotEmpty
        private String password;
        @NotEmpty
        private String gubun;

        public Transaction toEntity(Account withdrawAccount) {
            Transaction transaction = Transaction.builder()
                    .withdrawAccount(withdrawAccount)
                    .depositAccount(null)
                    .withdrawAccountBalance(withdrawAccount.getBalance())
                    .amount(amount)
                    .gubun(TransactionEnum.valueOf(gubun))
                    .build();
            return transaction;
        }
    }

    @Setter
    @Getter
    public static class DepositReqDto {
        private Long number;
        private Long amount;
        private String gubun;

        public Transaction toEntity(Account deposiAccount) {
            Transaction transaction = Transaction.builder()
                    .withdrawAccount(null)
                    .depositAccount(deposiAccount)
                    .depositAccountBalance(deposiAccount.getBalance())
                    .amount(amount)
                    .gubun(TransactionEnum.valueOf(gubun))
                    .build();
            return transaction;
        }
    }
}
