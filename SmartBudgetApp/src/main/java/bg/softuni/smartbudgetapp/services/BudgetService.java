package bg.softuni.smartbudgetapp.services;

import bg.softuni.smartbudgetapp.models.CategoryEnum;
import bg.softuni.smartbudgetapp.models.dto.BudgetDTO;

import java.util.List;

public interface BudgetService {

    List<BudgetDTO> getAllBudgetsForUser(Long userId);

    BudgetDTO createBudget(BudgetDTO budgetDTO, Long userId);

    double getMonthlyLimit(Long userId, CategoryEnum category);

    Double getTotalBudgetAmountByAccountId(Long accountId);

    Double getLimitByAccountIdAndCategory(Long accountId, CategoryEnum category);


    /**
     * Извлича списък с категории, които имат активни бюджети за дадена сметка.
     * Активен бюджет е такъв, който има останали средства (monthlyLimit > 0).
     *
     * @param accountId Идентификатор на сметката
     * @return Списък с активни категории
     */
    List<CategoryEnum> getActiveCategoriesForAccount(Long accountId);

    // метод за взимане на бюджети по сметка
    List<BudgetDTO> getBudgetsByAccountId(Long accountId);
}
