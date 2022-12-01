package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@RequiredArgsConstructor
@Configuration
public class DevInit extends DummyEntity {

    @Profile("dev")
    @Bean
    public CommandLineRunner dataSetting(UserRepository userRepository, AccountRepository accountRepository) {

        return (args) -> {
            User ssar = userRepository.save(newUser("ssar"));
            Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
            Account ssarAccount2 = accountRepository.save(newAccount(2222L, ssar));
        };
    }
}
