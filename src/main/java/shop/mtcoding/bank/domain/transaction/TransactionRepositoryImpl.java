package shop.mtcoding.bank.domain.transaction;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface Dao {
    List<Transaction> findAllByAccountId(Long accountId);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {

    private final EntityManager em;

    @Override
    public List<Transaction> findAllByAccountId(Long accountId) {

        return null;
    }

}
