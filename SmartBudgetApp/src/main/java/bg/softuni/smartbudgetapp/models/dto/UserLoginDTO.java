package bg.softuni.smartbudgetapp.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserLoginDTO {

    @NotBlank (message = "Полето не може да бъде празно")
    @Email(message = "Моля попълнете валиден email  ")
    private String email;

    @NotBlank(message = "Полето не може да бъде празно")
    @Size(min = 6, message = "Паролата трябва да бъде най-малко 6 символа")
    private String password;

    public UserLoginDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
