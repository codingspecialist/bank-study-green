package shop.mtcoding.bank.domain.transaction;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.config.enums.TransactionEnum;

interface Dao {
    List<Transaction> findAllByAccountId(Long accountId, String gubun, Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {

    private final EntityManager em;

    /**
     * 입금 내역보기 (select t from Transaction t join fetch t.depositAccount da where
     * da.number = 1111)
     */
    /**
     * 출금 내역보기 (select t from Transaction t join fetch t.withdrawAccount wa where
     * wa.number = 1111)
     */
    /**
     * 입출금 내역보기 (select t from Transaction t left join t.withdrawAccount wa left
     * join t.depositAccount da where da.number = 1111 or wa.number = 1111)
     */
    @Override
    public List<Transaction> findAllByAccountId(Long accountId, String gubun, Integer page) {
        String sql = "";
        sql += "select t from Transaction t ";
        sql += "left join t.withdrawAccount wa ";
        sql += "left join t.depositAccount da ";

        if (gubun == null || gubun.isEmpty()) {
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.DEPOSIT) {
            sql += "where t.depositAccount.id = :depositAccountId";
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.WITHDRAW) {
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if (gubun == null || gubun.isEmpty()) {
            query = query.setParameter("withdrawAccountId", accountId);
            query = query.setParameter("depositAccountId", accountId);
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.DEPOSIT) {
            query = query.setParameter("depositAccountId", accountId);
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.WITHDRAW) {
            query = query.setParameter("withdrawAccountId", accountId);
        }

        query.setFirstResult(page * 3);
        query.setMaxResults(3);
        return query.getResultList();
    }

}
