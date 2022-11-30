package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class DevInit {

    @Profile("dev")
    @Bean
    public CommandLineRunner dataSetting() {

        return (args) -> {
        };
    }
}
