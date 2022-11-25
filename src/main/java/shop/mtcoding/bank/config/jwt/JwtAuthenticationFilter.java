package shop.mtcoding.bank.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.UserReqDto.LoginReqDto;
import shop.mtcoding.bank.dto.UserRespDto.LoginRespDto;
import shop.mtcoding.bank.util.CustomResponseUtil;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    // post의 /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        log.debug("디버그 : attemptAuthentication 요청됨");

        try {
            ObjectMapper om = new ObjectMapper();
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(),
                    loginReqDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            log.debug("디버그5 authentication : null이 리턴됨");
            return authentication;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        CustomResponseUtil.fail(response, "로그인 실패");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        log.debug("디버그 : successfulAuthentication 요청됨");
        // 1. 세션에 있는 UserDetails 가져오기
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();

        // 2. 세션값으로 토큰 생성
        String jwtToken = JwtProcess.create(loginUser);

        // 3. 토큰을 헤더에 담기
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        // 4. 토큰 담아서 성공 응답하기
        LoginRespDto loginRespDto = new LoginRespDto(loginUser.getUser());
        CustomResponseUtil.success(response, loginRespDto);
    }

}
