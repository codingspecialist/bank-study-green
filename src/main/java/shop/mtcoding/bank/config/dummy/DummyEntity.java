package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.mtcoding.bank.config.enums.TransactionEnum;
import shop.mtcoding.bank.config.enums.UserEnum;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;

public abstract class DummyEntity {

    protected User newUser(String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        User user = User.builder()
                .username(username)
                .password(encPassword)
                .fullName(username)
                .email(username + "@nate.com")
                .role(username.equals("admin") ? UserEnum.ADMIN : UserEnum.CUSTOMER)
                .build();
        return user;
    }

    protected Account newAccount(Long number, User user) {
        Account account = Account.builder()
                .number(number)
                .password("1234")
                .balance(1000L)
                .user(user)
                .isActive(true)
                .build();
        return account;
    }

    protected Transaction newTransaction(Account withdrawAccount, Account depositAccount, String gubun) {
        Long withdrawAccountBalance = null;
        Long depositAccountBalance = null;

        if (withdrawAccount != null) {
            withdrawAccountBalance = withdrawAccount.getBalance();
        }

        if (depositAccount != null) {
            depositAccountBalance = depositAccount.getBalance();
        }

        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .withdrawAccountBalance(withdrawAccountBalance)
                .depositAccountBalance(depositAccountBalance)
                .amount(100L)
                .gubun(TransactionEnum.valueOf(gubun))
                .build();
        return transaction;
    }
}
