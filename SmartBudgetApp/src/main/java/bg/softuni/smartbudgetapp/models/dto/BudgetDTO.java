package bg.softuni.smartbudgetapp.models.dto;

import bg.softuni.smartbudgetapp.models.CategoryEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BudgetDTO {
    private Long id;

    @NotNull(message = "Категорията е задължителна")
    private CategoryEnum category;

    @NotNull(message = "Месечният лимит е задължителен")
    @Positive(message = "Лимитът трябва да е положително число")
    private Double monthlyLimit;

    @NotNull(message = "Трябва да изберете сметка")
    private Long accountId;

    // Добавяме име на сметка за показване в таблицата
    private String accountName;

    public BudgetDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}


