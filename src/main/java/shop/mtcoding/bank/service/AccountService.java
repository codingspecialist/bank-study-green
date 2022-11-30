package shop.mtcoding.bank.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.config.exception.CustomApiException;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.AccountReqDto.AccountDeleteReqDto;
import shop.mtcoding.bank.dto.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.bank.dto.AccountRespDto.AccountListRespDto;
import shop.mtcoding.bank.dto.AccountRespDto.AccountListRespDtoV3;
import shop.mtcoding.bank.dto.AccountRespDto.AccountSaveRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    // 계좌상세보기 (Account + List<Transaction>) Transaction 구현하고 만들기

    // 본인계좌삭제하기
    @Transactional
    public void 본인_계좌삭제(AccountDeleteReqDto accountDeleteReqDto, Long userId, Long accountId) {
        // 계좌 확인 (있는지 여부)
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomApiException("해당 계좌가 없습니다.", HttpStatus.BAD_REQUEST));

        // 계좌 삭제하기
        account.deleteAccount(userId, accountDeleteReqDto.getPassword());
    } // 더티체킹 (update 문 전송됨)

    // 본인계좌목록보기
    public AccountListRespDto 본인_계좌목록보기(Long userId) {
        List<Account> accountListPS = accountRepository.findByActiveUserId(userId);
        if (accountListPS.size() == 0) {
            User userPS = userRepository.findById(userId).orElseThrow(
                    () -> new CustomApiException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
            return new AccountListRespDto(userPS);
        } else {
            return new AccountListRespDto(accountListPS);
        }
    }

    // select 두번
    public AccountListRespDto 본인_계좌목록보기v2(Long userId) {
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new CustomApiException("유저 못찾음", HttpStatus.BAD_REQUEST));
        List<Account> accountListPS = accountRepository.findByActiveUserIdv2(userId);
        return new AccountListRespDto(userPS, accountListPS);
    }

    // 양방향 매핑
    public AccountListRespDtoV3 본인_계좌목록보기v3(Long userId) {
        User user = userRepository.findByActiveUserIdv3(userId);
        return new AccountListRespDtoV3(user);
    }

    @Transactional
    public AccountSaveRespDto 계좌생성(AccountSaveReqDto accountSaveReqDto, Long userId) {
        log.debug("디버그 : 계좌생성 서비스 호출됨");
        // 1. 검증(권한, 값 검증)
        User userPS = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomApiException("탈퇴된 유저로 계좌를 생성할 수 없습니다.", HttpStatus.FORBIDDEN));

        // 2. 실행
        Account account = accountSaveReqDto.toEntity(userPS);
        Account accountPS = accountRepository.save(account);

        // 3. DTO 응답
        return new AccountSaveRespDto(accountPS);
    }

}
