package shop.mtcoding.bank.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.dto.ResponseDto;

@Component
public class CustomLoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

        private final Logger log = LoggerFactory.getLogger(getClass());

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                log.debug("디버그 : onAuthenticationSuccess 실행됨");
        }

        // ControllerAdvice가 제어하지 못한다.
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException exception) throws IOException, ServletException {
                log.debug("디버그 : onAuthenticationFailure 실행됨");
                ObjectMapper om = new ObjectMapper();
                ResponseDto<?> responseDto = new ResponseDto<>("로그인 실패", null);
                String responseBody = om.writeValueAsString(responseDto);
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(400);
                response.getWriter().println(responseBody);
        }

}
