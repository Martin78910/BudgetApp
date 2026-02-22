package bg.softuni.smartbudgetapp.repositories;

import bg.softuni.smartbudgetapp.models.BudgetEntity;
import bg.softuni.smartbudgetapp.models.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {

    @Query("SELECT b.monthlyLimit FROM BudgetEntity b WHERE b.account.id = :accountId AND b.category = :category")
    Double findLimitByAccountIdAndCategory(@Param("accountId") Long accountId,
                                           @Param("category") CategoryEnum category);

    @Query("SELECT COALESCE(SUM(b.monthlyLimit), 0) FROM BudgetEntity b WHERE b.account.id = :accountId")
    Double getTotalBudgetAmountByAccountId(@Param("accountId") Long accountId);


    @Query("SELECT DISTINCT b.category FROM BudgetEntity b " +
            "WHERE b.account.id = :accountId AND b.monthlyLimit > 0")
    List<CategoryEnum> findActiveCategoriesForAccount(@Param("accountId") Long accountId);


}
