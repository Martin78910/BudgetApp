package bg.softuni.smartbudgetapp.services.impl;


import bg.softuni.smartbudgetapp.models.AccountEntity;
import bg.softuni.smartbudgetapp.models.BudgetEntity;
import bg.softuni.smartbudgetapp.models.CategoryEnum;
import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.BudgetDTO;
import bg.softuni.smartbudgetapp.repositories.AccountRepository;
import bg.softuni.smartbudgetapp.repositories.BudgetRepository;
import bg.softuni.smartbudgetapp.repositories.TransactionRepository;
import bg.softuni.smartbudgetapp.repositories.UserRepository;
import bg.softuni.smartbudgetapp.services.BudgetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public BudgetServiceImpl(BudgetRepository budgetRepository,
                             UserRepository userRepository,
                             AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;

    }

    @Override
    public List<BudgetDTO> getAllBudgetsForUser(Long userId) {
        List<BudgetEntity> allBudgets = budgetRepository.findAll();

        return allBudgets.stream()
                .filter(b -> b.getUser() != null && b.getUser().getId().equals(userId))
                .map(this::mapEntityToDTO)
                .toList();
    }

    @Override
    public BudgetDTO createBudget(BudgetDTO budgetDTO, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BudgetEntity budgetEntity = new BudgetEntity();
        budgetEntity.setCategory(budgetDTO.getCategory());
        budgetEntity.setMonthlyLimit(budgetDTO.getMonthlyLimit());
        budgetEntity.setUser(user);

        AccountEntity account = accountRepository.findById(budgetDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        budgetEntity.setAccount(account);

        BudgetEntity saved = budgetRepository.save(budgetEntity);

        return mapEntityToDTO(saved);
    }

    @Override
    public double getMonthlyLimit(Long userId, CategoryEnum category) {
        return budgetRepository.findAll().stream()
                .filter(b -> b.getUser() != null
                        && b.getUser().getId().equals(userId)
                        && b.getCategory() == category)
                .findFirst()
                .map(BudgetEntity::getMonthlyLimit)
                .orElse(0.0);
    }


    @Override
    public List<CategoryEnum> getActiveCategoriesForAccount(Long accountId) {
        return budgetRepository.findAll().stream()
                .filter(budget ->
                        budget.getAccount() != null &&
                                budget.getAccount().getId().equals(accountId) &&
                                budget.getMonthlyLimit() > 0
                )
                .map(BudgetEntity::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Double getLimitByAccountIdAndCategory(Long accountId, CategoryEnum category) {
        return budgetRepository.findLimitByAccountIdAndCategory(accountId, category);
    }

    @Override
    public List<BudgetDTO> getBudgetsByAccountId(Long accountId) {
        return budgetRepository.findAll().stream()
                .filter(b -> b.getAccount() != null && b.getAccount().getId().equals(accountId))
                .map(this::mapEntityToDTO)
                .toList();
    }


    @Override
    public Double getTotalBudgetAmountByAccountId(Long accountId) {
        return budgetRepository.getTotalBudgetAmountByAccountId(accountId);
    }

    private BudgetDTO mapEntityToDTO(BudgetEntity entity) {
        BudgetDTO dto = new BudgetDTO();
        dto.setId(entity.getId());
        dto.setCategory(entity.getCategory());
        dto.setMonthlyLimit(entity.getMonthlyLimit());

        if (entity.getAccount() != null) {
            dto.setAccountId(entity.getAccount().getId());
            dto.setAccountName(entity.getAccount().getAccountName());
        }

        return dto;
    }

}
