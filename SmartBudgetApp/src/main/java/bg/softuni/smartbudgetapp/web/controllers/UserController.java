package bg.softuni.smartbudgetapp.web.controllers;

import bg.softuni.smartbudgetapp.models.CurrencyEntity;
import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.UserLoginDTO;
import bg.softuni.smartbudgetapp.models.dto.UserProfileDTO;
import bg.softuni.smartbudgetapp.models.dto.UserRegisterDTO;
import bg.softuni.smartbudgetapp.services.CurrencyService;
import bg.softuni.smartbudgetapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrencyService currencyService;

    public UserController(UserService userService, CurrencyService currencyService) {
        this.userService = userService;
        this.currencyService = currencyService;
    }

    // GET: /users/register
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Ако липсва атрибут userRegisterDTO, създаваме нов
        if (!model.containsAttribute("userRegisterDTO")) {
            model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        }
        return "register";
    }

    // POST: /users/register
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
            BindingResult bindingResult,
            Model model) {

        // Ако има грешки при валидацията, връщаме същата страница
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Тук може да проверите дали password и confirmPassword съвпадат и т.н.
        // Преобразуваме DTO към UserEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userRegisterDTO.getEmail());
        userEntity.setPassword(userRegisterDTO.getPassword());
        userEntity.setFullName(userRegisterDTO.getFullName());

        // Извикваме сървиса, за да запишем в базата
        userService.registerUser(userEntity, "ROLE_USER");

        // Пренасочваме към /login (или където желаете)
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String LoginForm(Model model) {
        // Ако в model-а няма userLoginDTO, създаваме нов
        if (!model.containsAttribute("userLoginDTO")) {
            model.addAttribute("userLoginDTO", new UserLoginDTO());
        }
        return "login";
    }



    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        // 1. Намираме email-a на логнатия потребител
        String currentUserEmail = principal.getName();

        // 2. Търсим го в базата
        UserEntity user = userService.findByEmail(currentUserEmail);
        if (user == null) {
            throw new RuntimeException("Logged user not found in DB!");
        }

        // 3. Слагаме го в model
        model.addAttribute("user", user);


        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName(user.getFullName());
        userProfileDTO.setCurrency(user.getCurrency());
        userProfileDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        model.addAttribute("userProfileDTO", userProfileDTO);

        // 3. Взимаме списъка валути от базата

        model.addAttribute("allCurrencies", currencyService.getAllCurrencies());

        model.addAttribute("user", user);

        return "profile";
    }


    @GetMapping("/profile/update")
    public String showUpdateProfileForm(Model model, Principal principal) {

        // 1) Намираме логнатия потребител по email
        String currentUserEmail = principal.getName();
        UserEntity user = userService.findByEmail(currentUserEmail);
        if (user == null) {
            throw new RuntimeException("Logged user not found in DB!");
        }

        // 2) Подготвяме DTO
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName(user.getFullName());
        userProfileDTO.setCurrency(user.getCurrency());
        userProfileDTO.setProfilePictureUrl(user.getProfilePictureUrl());

        model.addAttribute("userProfileDTO", userProfileDTO);

        // зареждаме валутите
        model.addAttribute("allCurrencies", currencyService.getAllCurrencies());

        // 3) Връщаме update-profile.html
        return "update-profile";
    }



    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("userProfileDTO") UserProfileDTO userProfileDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) {

        // 1. Ако има валидационни грешки, връщаме същата страница
        if (bindingResult.hasErrors()) {
            return "update-profile"; // или името на profile шаблон
        }

        // 2. Откриваме текущо логнатия потребител (по email)
        // principal.getName() връща email/username (проверете в UserDetailsService).
        String currentUserEmail = principal.getName();

        UserEntity existingUser = userService.findByEmail(currentUserEmail);
        if (existingUser == null) {
            throw new RuntimeException("Currently logged user not found in DB!");
        }

        // 3. Пренасяме полетата от DTO към UserEntity
        // (Password ще се обновява само ако е непразна, както е в updateUserProfile логиката.)
        existingUser.setFullName(userProfileDTO.getFullName());
        existingUser.setCurrency(userProfileDTO.getCurrency());
        existingUser.setProfilePictureUrl(userProfileDTO.getProfilePictureUrl());

        // 4. Ъпдейтваме в базата
        // Вътре в updateUserProfile(...) ще се извършат допълнителни проверки,
        // и ако има нова парола, ще се кодира.
        userService.updateUserProfile(existingUser);

        // 5. Пренасочваме отново към профила
        return "redirect:/users/profile";
    }



}
