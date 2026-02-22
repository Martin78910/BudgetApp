package bg.softuni.smartbudgetapp.services.impl;

import bg.softuni.smartbudgetapp.models.AccountEntity;
import bg.softuni.smartbudgetapp.models.CategoryEnum;
import bg.softuni.smartbudgetapp.models.TransactionEntity;
import bg.softuni.smartbudgetapp.models.dto.BudgetReportDTO;
import bg.softuni.smartbudgetapp.models.dto.TransactionDTO;
import bg.softuni.smartbudgetapp.repositories.AccountRepository;
import bg.softuni.smartbudgetapp.repositories.TransactionRepository;
import bg.softuni.smartbudgetapp.services.BudgetService;
import bg.softuni.smartbudgetapp.services.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BudgetService budgetService;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.budgetService = budgetService;
    }

    @Override
    public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {
        // Филтриране на транзакциите по ID на сметка
        List<TransactionEntity> transactions = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getAccount() != null && t.getAccount().getId().equals(accountId))
                .collect(Collectors.toList());

        // Конвертиране на ентитита към DTO обекти
        return transactions.stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO addTransaction(TransactionDTO transactionDTO) {
        // Допълнителна валидация за разходни трансакции
        if (!transactionDTO.isIncome()) {
            Long accountId = transactionDTO.getAccountId();
            CategoryEnum category = transactionDTO.getCategory();

            // Извличане на месечния лимит за категорията
            Double monthlyLimit = budgetService.getLimitByAccountIdAndCategory(accountId, category);

            // Проверка дали лимитът е дефиниран
            if (monthlyLimit != null && monthlyLimit > 0) {
                // Изчисляване на досегашните разходи
                double spentSoFar = getTotalExpensesByAccountAndCategory(accountId, category);

                // Проверка дали новата трансакция ще надвиши бюджета
                if (spentSoFar + transactionDTO.getAmount() > monthlyLimit) {
                    throw new RuntimeException(
                            "Надвишен бюджет за категория " + category +
                                    ". Налични средства: " + (monthlyLimit - spentSoFar)
                    );
                }
            }
        }

        // Намиране на сметката
        AccountEntity account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Сметката не е намерена"));

        // Създаване на нова транзакция
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(transactionDTO.getAmount());
        transactionEntity.setDescription(transactionDTO.getDescription());
        transactionEntity.setCategory(transactionDTO.getCategory());
        transactionEntity.setIncome(transactionDTO.isIncome());

        // Използване на подадена дата или текуща дата
        transactionEntity.setTransactionDate(
                transactionDTO.getTransactionDate() != null
                        ? transactionDTO.getTransactionDate()
                        : LocalDateTime.now()
        );
        transactionEntity.setAccount(account);

        // Запазване на трансакцията
        TransactionEntity savedEntity = transactionRepository.save(transactionEntity);
        return mapEntityToDTO(savedEntity);
    }

    // Помощен метод за конвертиране на ентити към DTO
    private TransactionDTO mapEntityToDTO(TransactionEntity entity) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setIncome(entity.isIncome());
        dto.setTransactionDate(entity.getTransactionDate());

        // Добавяне на ID на сметката, ако съществува
        if (entity.getAccount() != null) {
            dto.setAccountId(entity.getAccount().getId());
        }

        return dto;
    }

    @Override
    public double getSpentForCategory(Long userId, CategoryEnum category) {
        // Изчисляване на разходите за потребител и категория
        List<TransactionEntity> allTransactions = transactionRepository.findAll();
        return allTransactions.stream()
                .filter(t -> t.getAccount() != null
                        && t.getAccount().getOwner() != null
                        && t.getAccount().getOwner().getId().equals(userId)
                        && t.getCategory() == category
                        && !t.isIncome() // Само разходи
                )
                .mapToDouble(TransactionEntity::getAmount)
                .sum();
    }

    @Override
    public double getTotalSpendingForCategory(Long userId, CategoryEnum category) {
        // Извличане на общите разходи за категория и потребител
        Double sum = transactionRepository.getTotalSpendingForCategory(userId, category);
        return (sum != null) ? sum : 0.0;
    }

    @Override
    public double getTotalExpensesByAccountAndCategory(Long accountId, CategoryEnum category) {
        // Извличане на разходите за сметка и категория
        Double sum = transactionRepository.getTotalExpensesByAccountAndCategory(accountId, category);
        return (sum != null) ? sum : 0.0;
    }


    /**
     * Извлича данни за отчета на бюджета за конкретен месец и година.
     * Групира разходите по категория и изчислява общата сума за всяка категория.
     *
     * @param userId ID на потребителя
     * @param month Месец (1-12)
     * @param year Година
     * @return Списък от DTO обекти с информация за всяка категория разходи
     */
    @Override
    public List<BudgetReportDTO> getBudgetReportData(Long userId, int month, int year) {
        // Създаваме начална и крайна дата за месеца
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        // Получаваме всички сметки на потребителя
        List<AccountEntity> userAccounts = accountRepository.findByOwner_Id(userId);
        List<Long> accountIds = userAccounts.stream()
                .map(AccountEntity::getId)
                .collect(Collectors.toList());

        // Извличаме всички разходни транзакции за сметките на потребителя в избрания месец
        List<TransactionEntity> transactions = transactionRepository.findByAccountIdInAndTransactionDateBetweenAndIncomeIsFalse(
                accountIds, startOfMonth, endOfMonth);

        // Групираме транзакциите по категория и калкулираме общата сума за всяка категория
        Map<CategoryEnum, Double> categoryTotals = new HashMap<>();

        for (TransactionEntity transaction : transactions) {
            CategoryEnum category = transaction.getCategory();
            double amount = transaction.getAmount();

            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        // Преобразуваме категориите и сумите в списък от DTO обекти
        List<BudgetReportDTO> reportData = new ArrayList<>();

        for (Map.Entry<CategoryEnum, Double> entry : categoryTotals.entrySet()) {
            String categoryName = entry.getKey().toString();
            double amount = entry.getValue();

            reportData.add(new BudgetReportDTO(categoryName, amount));
        }

        return reportData;
    }
}
