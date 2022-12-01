package shop.mtcoding.bank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.config.enums.TransactionEnum;
import shop.mtcoding.bank.config.exception.CustomApiException;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.dto.TransactionReqDto.DepositReqDto;
import shop.mtcoding.bank.dto.TransactionReqDto.WithdrawReqDto;
import shop.mtcoding.bank.dto.TransactionRespDto.DepositRespDto;
import shop.mtcoding.bank.dto.TransactionRespDto.WithdrawRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    // /api/user/{id}/withdraw
    // /api/account/{number}/withdraw
    @Transactional
    public WithdrawRespDto 출금하기(WithdrawReqDto withdrawReqDto, Long number, Long userId) {
        // 구분값 검증
        if (TransactionEnum.valueOf(withdrawReqDto.getGubun()) != TransactionEnum.WITHDRAW) {
            throw new CustomApiException("구분값 검증 실패", HttpStatus.BAD_REQUEST);
        }

        // 출금계좌 존재 확인
        Account withdrawAccountPS = accountRepository.findByNumber(number)
                .orElseThrow(() -> new CustomApiException("해당 계좌 없음", HttpStatus.BAD_REQUEST));

        // 0원 체크
        if (withdrawReqDto.getAmount() <= 0) {
            throw new CustomApiException("0원이 입금될 수 없습니다", HttpStatus.BAD_REQUEST);
        }

        // 출금계좌 소유자 확인 (number 튀어나온 계좌가 로그인한 사람의 계좌가 맞는지)
        withdrawAccountPS.isOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkPassword(withdrawReqDto.getPassword());

        // 출금하기 (계좌 잔액 수정, 트랜잭션 히스토리 인서트)
        withdrawAccountPS.withdraw(withdrawReqDto.getAmount()); // 더티체킹
        Transaction transaction = withdrawReqDto.toEntity(withdrawAccountPS);
        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 리턴
        return new WithdrawRespDto(transactionPS);
    }

    @Transactional
    public DepositRespDto 입금하기(DepositReqDto depositReqDto) {
        // 구분값 검증
        if (TransactionEnum.valueOf(depositReqDto.getGubun()) != TransactionEnum.DEPOSIT) {
            throw new CustomApiException("구분값 검증 실패", HttpStatus.BAD_REQUEST);
        }

        // 입금계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(depositReqDto.getNumber())
                .orElseThrow(() -> new CustomApiException("해당 계좌 없음", HttpStatus.BAD_REQUEST));

        // 0원 체크
        if (depositReqDto.getAmount() <= 0) {
            throw new CustomApiException("0원이 입금될 수 없습니다", HttpStatus.BAD_REQUEST);
        }

        // 실행
        depositAccountPS.입금하기(depositReqDto.getAmount()); // 더티체킹 (update)
        Transaction transaction = depositReqDto.toEntity(depositAccountPS);
        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 응답
        return new DepositRespDto(transactionPS);
    }

}
