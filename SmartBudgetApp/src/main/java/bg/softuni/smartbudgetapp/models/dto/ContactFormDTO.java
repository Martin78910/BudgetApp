package bg.softuni.smartbudgetapp.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactFormDTO {

    @NotBlank(message = "Моля, въведете вашето име.")
    @Size(min = 2, max = 50, message = "Името трябва да е между 2 и 50 символа.")
    private String senderName;

    @NotBlank(message = "Моля, въведете имейл адрес.")
    @Email(message = "Моля, въведете валиден имейл адрес.")
    private String senderEmail;

    @NotBlank(message = "Моля, въведете съобщение.")
    @Size(min = 10, max = 1000, message = "Съобщението трябва да е между 10 и 1000 символа.")
    private String message;

    public ContactFormDTO() {
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
