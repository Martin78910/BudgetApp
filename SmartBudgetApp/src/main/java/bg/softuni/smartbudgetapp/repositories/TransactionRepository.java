package bg.softuni.smartbudgetapp.repositories;

import bg.softuni.smartbudgetapp.models.CategoryEnum;
import bg.softuni.smartbudgetapp.models.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByAccountId(Long accountId);

    // Връща сумата на всички outbound транзакции (income=false) за userId + category
    @Query("SELECT SUM(t.amount) " +
            "FROM TransactionEntity t " +
            "WHERE t.account.owner.id = :userId " +
            "  AND t.category = :category " +
            "  AND t.income = false")
    Double getTotalSpendingForCategory(@Param("userId") Long userId,
                                       @Param("category") CategoryEnum category);


    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "WHERE t.account.id = :accountId AND t.category = :category AND t.income = false")
    Double getTotalExpensesByAccountAndCategory(@Param("accountId") Long accountId,
                                                @Param("category") CategoryEnum category);


    // Премахнати дублирани методи - следващите два метода са дублиращи функционалността
    // на getTotalSpendingForCategory и getTotalExpensesByAccountAndCategory
    /*
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t " +
            "JOIN t.account a " +
            "JOIN a.user u " +
            "WHERE u.id = :userId AND t.category = :category AND t.income = false")
    double findTotalSpentByUserIdAndCategory(@Param("userId") Long userId,
                                             @Param("category") CategoryEnum category);

    @Query("SELECT SUM(t.amount) FROM TransactionEntity t " +
            "JOIN t.account a " +
            "JOIN a.user u " +
            "WHERE u.id = :userId AND t.category = :category")
    double findTotalSpendingByUserIdAndCategory(@Param("userId") Long userId,
                                                @Param("category") CategoryEnum category);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "WHERE t.account.id = :accountId AND t.category = :category AND t.income = false")
    double findTotalExpensesByAccountAndCategory(@Param("accountId") Long accountId,
                                                 @Param("category") CategoryEnum category);
    */

    /**
     * Намира всички транзакции за списък от сметки в определен период,
     * филтрирани по income флаг
     */
    List<TransactionEntity> findByAccountIdInAndTransactionDateBetweenAndIncomeIsFalse(
            List<Long> accountIds,
            LocalDateTime startDate,
            LocalDateTime endDate);

}