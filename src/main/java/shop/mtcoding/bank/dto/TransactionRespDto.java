package shop.mtcoding.bank.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.util.CustomDateUtil;

public class TransactionRespDto {

    @Setter
    @Getter
    public static class TransactionListRespDto {
        private List<TransactionDto> transactions = new ArrayList<>();

        public TransactionListRespDto(List<Transaction> transactions) {
            this.transactions = transactions.stream().map((transaction) -> new TransactionDto(transaction))
                    .collect(Collectors.toList());
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
    public static class TransferRespDto {
        private Long id;
        private Long amount;
        private String gubun;
        private String from;
        private String to;
        private Long withdrawAccountBalance;

        public TransferRespDto(Transaction transaction) {
            this.id = transaction.getId();
            this.amount = transaction.getAmount();
            this.gubun = transaction.getGubun().getValue(); // 이체
            this.from = transaction.getWithdrawAccount().getNumber() + "";
            this.to = transaction.getDepositAccount().getNumber() + "";
            this.withdrawAccountBalance = transaction.getWithdrawAccountBalance();
        }

    }

    @Setter
    @Getter
    public static class DepositRespDto {
        private Long id;
        private Long amount;
        private String gubun;
        private String from;
        private String to;
        @JsonIgnore
        private Long depositAccountBalance;

        public DepositRespDto(Transaction transaction) {
            this.id = transaction.getId();
            this.amount = transaction.getAmount();
            this.gubun = transaction.getGubun().getValue(); // 입금
            this.from = "ATM";
            this.to = transaction.getDepositAccount().getNumber() + "";
            this.depositAccountBalance = transaction.getDepositAccountBalance();
        }

    }

    @Setter
    @Getter
    public static class WithdrawRespDto {
        private Long id;
        private Long amount;
        private String gubun;
        private String from;
        private String to;
        private Long withdrawAccountBalance;

        public WithdrawRespDto(Transaction transaction) {
            this.id = transaction.getId();
            this.amount = transaction.getAmount();
            this.gubun = transaction.getGubun().getValue(); // 출금
            this.from = transaction.getWithdrawAccount().getNumber() + "";
            this.to = "ATM";
            this.withdrawAccountBalance = transaction.getWithdrawAccountBalance();
        }

    }
}
