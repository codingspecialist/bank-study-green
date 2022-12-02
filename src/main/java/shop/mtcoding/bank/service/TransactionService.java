package shop.mtcoding.bank.service;

import java.util.List;

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
import shop.mtcoding.bank.dto.TransactionReqDto.TransferReqDto;
import shop.mtcoding.bank.dto.TransactionReqDto.WithdrawReqDto;
import shop.mtcoding.bank.dto.TransactionRespDto.DepositRespDto;
import shop.mtcoding.bank.dto.TransactionRespDto.TransactionListRespDto;
import shop.mtcoding.bank.dto.TransactionRespDto.TransferRespDto;
import shop.mtcoding.bank.dto.TransactionRespDto.WithdrawRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionListRespDto 입출금목록보기(Long userId, Long accountId, String gubun, Integer page) {
        // 해당 계좌 존재 여부
        Account accountPS = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomApiException("해당 계좌 없음", HttpStatus.BAD_REQUEST));

        // 계좌 소유자 확인
        accountPS.isOwner(userId);

        // 입출금 내역조회
        List<Transaction> transactionListPS = transactionRepository.findAllByAccountId(accountId, gubun, page);
        return new TransactionListRespDto(transactionListPS);
    }

    @Transactional
    public TransferRespDto 이체하기(TransferReqDto transferReqDto, Long withdrawNumber, Long depositNumber,
            Long userId) {
        // 구분값 검증
        if (TransactionEnum.valueOf(transferReqDto.getGubun()) != TransactionEnum.TRANSFER) {
            throw new CustomApiException("구분값 검증 실패", HttpStatus.BAD_REQUEST);
        }

        // 출금계좌와 입금계좌가 동일하면 거부
        if (withdrawNumber == depositNumber) {
            throw new CustomApiException("입출금계좌가 동일할 수 없습니다", HttpStatus.BAD_REQUEST);
        }

        // 출금계좌 존재 확인
        Account withdrawAccountPS = accountRepository.findByNumber(withdrawNumber)
                .orElseThrow(() -> new CustomApiException("출금 계좌 없음", HttpStatus.BAD_REQUEST));

        // 입금계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(depositNumber)
                .orElseThrow(() -> new CustomApiException("입금 계좌 없음", HttpStatus.BAD_REQUEST));

        // 0원 체크
        if (transferReqDto.getAmount() <= 0) {
            throw new CustomApiException("0원이 입금될 수 없습니다", HttpStatus.BAD_REQUEST);
        }

        // 출금계좌 소유자 체크
        withdrawAccountPS.isOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkPassword(transferReqDto.getPassword());

        // 이체하기 (출금계좌 금액 변경, 입금계좌 금액 변경, 트랜잭션 히스토리 생성)
        withdrawAccountPS.withdraw(transferReqDto.getAmount()); // 더티체킹
        depositAccountPS.입금하기(transferReqDto.getAmount()); // 더티체킹

        Transaction transaction = transferReqDto.toEntity(withdrawAccountPS, depositAccountPS);
        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 응답
        return new TransferRespDto(transactionPS);
    }

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
