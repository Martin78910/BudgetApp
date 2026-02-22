package bg.softuni.smartbudgetapp.models.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterDTO {

    @NotBlank (message = "Полето не може да бъде празно")
    @Email(message = "Моля попълнете валиден email")
    private String email;

    @NotBlank (message = "Полето не може да бъде празно")
    @Size(min = 6, message = "Паролата трябва да бъде най-малко 6 символа")
    private String password;

    @NotBlank (message = "Полето не може да бъде празно")
    private String confirmPassword;

    @Size(min = 3, message = "Името трябва да съдържа поне 3 символа")
    private String fullName;

    public UserRegisterDTO() {
    }

    public @Size(min = 3, message = "Името трябва да съдържа поне 3 символа") String getFullName() {
        return fullName;
    }

    public void setFullName(@Size(min = 3, message = "Името трябва да съдържа поне 3 символа") String fullName) {
        this.fullName = fullName;
    }

    public @NotBlank(message = "Полето не може да бъде празно") String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@NotBlank(message = "Полето не може да бъде празно") String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public @NotBlank(message = "Полето не може да бъде празно") @Size(min = 6, message = "Паролата трябва да бъде най-малко 6 символа") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Полето не може да бъде празно") @Size(min = 6, message = "Паролата трябва да бъде най-малко 6 символа") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Полето не може да бъде празно") @Email(message = "Моля попълнете валиден email") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Полето не може да бъде празно") @Email(message = "Моля попълнете валиден email") String email) {
        this.email = email;
    }
}
