package bg.softuni.smartbudgetapp.services;

import bg.softuni.smartbudgetapp.models.CategoryEnum;
import bg.softuni.smartbudgetapp.models.dto.BudgetReportDTO;
import bg.softuni.smartbudgetapp.models.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {

    List<TransactionDTO> getTransactionsByAccountId(Long accountId);
    TransactionDTO addTransaction(TransactionDTO transactionDTO);
    double getSpentForCategory(Long userId, CategoryEnum category);
    // връща общите разходи (outbound) за даден userId и категория
    double getTotalSpendingForCategory(Long userId, CategoryEnum category);
    double getTotalExpensesByAccountAndCategory(Long accountId, CategoryEnum category);
    List<BudgetReportDTO> getBudgetReportData(Long userId, int month, int year);
}
