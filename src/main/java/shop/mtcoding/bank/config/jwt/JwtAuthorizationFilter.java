package shop.mtcoding.bank.config.jwt;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
            UserRepository userRepository) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        String token = request.getHeader(JwtProperties.HEADER_STRING)
                .replace(JwtProperties.TOKEN_PREFIX, "");

        try {
            String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                    .getClaim("username").asString();

            Optional<User> userOP = userRepository.findByUsername(username);

            if (userOP.isPresent()) {
                LoginUser loginUser = new LoginUser(userOP.get());
                // Authentication authentication = new UsernamePasswordAuthenticationToken(
                // loginUser.getUsername(),
                // loginUser.getPassword());

                // SecurityContextHolder.getContext().setAuthentication(authentication);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword());

                authenticationManager.authenticate(authenticationToken);

                chain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}