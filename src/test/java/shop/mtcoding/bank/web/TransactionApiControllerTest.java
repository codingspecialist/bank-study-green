package shop.mtcoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.dummy.DummyEntity;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.TransactionReqDto.DepositReqDto;
import shop.mtcoding.bank.dto.TransactionReqDto.TransferReqDto;
import shop.mtcoding.bank.dto.TransactionReqDto.WithdrawReqDto;

@Sql("classpath:db/truncate.sql") // 롤백 대신 사용 (auto_increment 초기화 + 데이터 비우기)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class TransactionApiControllerTest extends DummyEntity {
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        User ssar = userRepository.save(newUser("ssar"));
        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account ssarAccount2 = accountRepository.save(newAccount(2222L, ssar));

        ssarAccount1.withdraw(100L);
        ssarAccount2.입금하기(100L);
        Transaction t1 = transactionRepository.save(newTransaction(ssarAccount1, ssarAccount2, "TRANSFER"));

        ssarAccount1.withdraw(100L);
        Transaction t2 = transactionRepository.save(newTransaction(ssarAccount1, null, "WITHDRAW"));

        ssarAccount1.입금하기(100L);
        Transaction t3 = transactionRepository.save(newTransaction(null, ssarAccount1, "DEPOSIT"));
    }

    @Test
    public void deposit_test() throws Exception {
        // given
        DepositReqDto depositReqDto = new DepositReqDto();
        depositReqDto.setNumber(1111L);
        depositReqDto.setAmount(300L);
        depositReqDto.setGubun("DEPOSIT");
        String requestBody = om.writeValueAsString(depositReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/deposit").content(requestBody)
                        .contentType(APPLICATION_JSON_UTF8));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.data.from").value("ATM"));
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_test() throws Exception {
        // given
        Long number = 1111L;

        WithdrawReqDto withdrawReqDto = new WithdrawReqDto();
        withdrawReqDto.setPassword("1234");
        withdrawReqDto.setAmount(500L);
        withdrawReqDto.setGubun("WITHDRAW");
        String requestBody = om.writeValueAsString(withdrawReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/account/" + number + "/withdraw").content(requestBody)
                        .contentType(APPLICATION_JSON_UTF8));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.data.withdrawAccountBalance").value(500));
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void transfer_test() throws Exception {
        // given
        Long withdrawNumber = 1111L;
        Long depositNumber = 2222L;

        TransferReqDto transferReqDto = new TransferReqDto();
        transferReqDto.setPassword("1234");
        transferReqDto.setAmount(500L);
        transferReqDto.setGubun("TRANSFER");
        String requestBody = om.writeValueAsString(transferReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/withdraw/" + withdrawNumber + "/deposit/" + depositNumber + "/transfer")
                        .content(requestBody)
                        .contentType(APPLICATION_JSON_UTF8));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.data.withdrawAccountBalance").value(500));
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void transactionList_test() throws Exception {
        // given
        Long accountId = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/account/" + accountId + "/transaction")
                        .param("page", "0"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.transactions.length()").value(3));
    }

}
