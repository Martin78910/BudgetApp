package bg.softuni.smartbudgetapp.models.dto;

import bg.softuni.smartbudgetapp.models.CurrencyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserProfileDTO {


    private String email;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, message = "Full name must be at least 2 characters")
    private String fullName;


    @NotNull(message = "Currency cannot be blank")
    private CurrencyEnum currency;

    // URL към профилната снимка,
    // може да бъде null, ако няма снимка
    private String profilePictureUrl;

    public UserProfileDTO() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @NotBlank(message = "Full name cannot be blank") @Size(min = 2, message = "Full name must be at least 2 characters") String getFullName() {
        return fullName;
    }

    public void setFullName(@NotBlank(message = "Full name cannot be blank") @Size(min = 2, message = "Full name must be at least 2 characters") String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public @NotNull(message = "Currency cannot be blank") CurrencyEnum getCurrency() {
        return currency;
    }

    public void setCurrency(@NotBlank(message = "Currency cannot be blank") CurrencyEnum currency) {
        this.currency = currency;
    }



}
