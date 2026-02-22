package bg.softuni.smartbudgetapp.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class AccountDTO {

    private Long id;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Account type is required")
    private String accountType; // "bank", "credit", "digital"...

    @NotNull
    @PositiveOrZero(message = "Balance cannot be negative")
    private Double balance;

    public AccountDTO() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Account name is required") String getAccountName() {
        return accountName;
    }

    public void setAccountName(@NotBlank(message = "Account name is required") String accountName) {
        this.accountName = accountName;
    }

    public @NotBlank(message = "Account type is required") String getAccountType() {
        return accountType;
    }

    public void setAccountType(@NotBlank(message = "Account type is required") String accountType) {
        this.accountType = accountType;
    }

    public @NotNull @PositiveOrZero(message = "Balance cannot be negative") Double getBalance() {
        return balance;
    }

    public void setBalance(@NotNull @PositiveOrZero(message = "Balance cannot be negative") Double balance) {
        this.balance = balance;
    }
}
