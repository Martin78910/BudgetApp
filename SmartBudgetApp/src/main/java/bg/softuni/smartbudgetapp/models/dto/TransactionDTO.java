package bg.softuni.smartbudgetapp.models.dto;

import bg.softuni.smartbudgetapp.models.CategoryEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TransactionDTO {

    private Long id;

    @NotNull
    @Min(value = 0, message = "Transaction amount must be >= 0")
    private Double amount;

    @Size(max = 200, message = "Description too long")
    private String description;

    @NotNull(message = "Category is required")
    private CategoryEnum category; // enum instead of String

    private boolean income;
    private LocalDateTime transactionDate;
    private Long accountId;

    public TransactionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull @Min(value = 0, message = "Transaction amount must be >= 0") Double getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @Min(value = 0, message = "Transaction amount must be >= 0") Double amount) {
        this.amount = amount;
    }

    public @Size(max = 200, message = "Description too long") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 200, message = "Description too long") String description) {
        this.description = description;
    }

    public @NotNull(message = "Category is required") CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(@NotNull(message = "Category is required") CategoryEnum category) {
        this.category = category;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
