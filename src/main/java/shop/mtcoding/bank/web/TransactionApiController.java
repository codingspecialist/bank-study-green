package shop.mtcoding.bank.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.TransactionReqDto.DepositReqDto;
import shop.mtcoding.bank.dto.TransactionReqDto.TransferReqDto;
import shop.mtcoding.bank.dto.TransactionReqDto.WithdrawReqDto;
import shop.mtcoding.bank.dto.TransactionRespDto.DepositRespDto;
import shop.mtcoding.bank.dto.TransactionRespDto.TransactionListRespDto;
import shop.mtcoding.bank.dto.TransactionRespDto.TransferRespDto;
import shop.mtcoding.bank.dto.TransactionRespDto.WithdrawRespDto;
import shop.mtcoding.bank.service.TransactionService;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TransactionApiController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final TransactionService transactionService;

    @PostMapping("/account/{number}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable Long number,
            @RequestBody WithdrawReqDto withdrawReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        WithdrawRespDto withdrawRespDto = transactionService.출금하기(withdrawReqDto, number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>("출금 완료", withdrawRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositReqDto depositReqDto) {
        DepositRespDto depositRespDto = transactionService.입금하기(depositReqDto);
        return new ResponseEntity<>(new ResponseDto<>("입금성공", depositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/withdraw/{withdrawNumber}/deposit/{depositNumber}/transfer")
    public ResponseEntity<?> transfer(
            @PathVariable Long withdrawNumber,
            @PathVariable Long depositNumber,
            @RequestBody TransferReqDto transferReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {
        TransferRespDto transferRespDto = transactionService.이체하기(transferReqDto, withdrawNumber, depositNumber,
                loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>("이체 성공", transferRespDto),
                HttpStatus.CREATED);
    }

    /*
     * 입출금 내역 보기 (동적 쿼리로 변경)
     */
    @GetMapping("/account/{accountId}/transaction")
    public ResponseEntity<?> transactionList(
            @RequestParam(value = "gubun", defaultValue = "") String gubun,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @PathVariable Long accountId,
            @AuthenticationPrincipal LoginUser loginUser) {
        TransactionListRespDto transactionListRespDto = transactionService.입출금목록보기(loginUser.getUser().getId(),
                accountId,
                gubun,
                page);
        return new ResponseEntity<>(new ResponseDto<>("입출금목록보기 성공", transactionListRespDto),
                HttpStatus.OK);
    }
}
