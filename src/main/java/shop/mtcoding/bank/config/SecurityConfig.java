package shop.mtcoding.bank.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import shop.mtcoding.bank.config.enums.UserEnum;
import shop.mtcoding.bank.config.jwt.JwtAuthenticationFilter;
import shop.mtcoding.bank.config.jwt.JwtAuthorizationFilter;
import shop.mtcoding.bank.util.CustomResponseUtil;

@Configuration
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그 : passwordEncoder Bean 등록됨");
        return new BCryptPasswordEncoder();
    }

    // 모든 필터 등록은 여기서!!
    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            log.debug("디버그 : SecurityConfig의 configure");
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http.addFilter(new JwtAuthenticationFilter(authenticationManager));
            http.addFilter(new JwtAuthorizationFilter(authenticationManager));
        }

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그 : SecurityConfig의 filterChain");
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.cors().configurationSource(configurationSource());

        // ExcpetionTranslationFilter (인증 권한 확인 필터)
        http.exceptionHandling().authenticationEntryPoint(
                (request, response, authException) -> {
                    CustomResponseUtil.forbidden(response, "권한없음");
                });

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.formLogin().disable();
        http.httpBasic().disable();
        http.apply(new MyCustomDsl());

        http.authorizeHttpRequests()
                .antMatchers("/api/transaction/**").authenticated()
                .antMatchers("/api/user/**").authenticated()
                .antMatchers("/api/account/**").authenticated()
                .antMatchers("/api/admin/**").hasRole("ROLE_" + UserEnum.ADMIN)
                .anyRequest().permitAll();

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        // configuration.setAllowedMethods(List.of("GET", "POST", "PUT"));

        // https://cotak.tistory.com/248
        configuration.addAllowedOriginPattern("*"); // 프론트 서버의 주소
        // configuration.addAllowedOrigin("*");
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키, 인증 이런 헤더

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
