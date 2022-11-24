package shop.mtcoding.bank.config.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import shop.mtcoding.bank.config.exception.CustomApiException;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Service
public class LoginUserService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("디버그 : loadUserByUsername 실행됨");
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new CustomApiException("username을 찾을 수 없습니다", HttpStatus.BAD_REQUEST));
        return new LoginUser(user);
    }

}
