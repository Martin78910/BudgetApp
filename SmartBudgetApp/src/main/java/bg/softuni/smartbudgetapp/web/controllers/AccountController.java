package bg.softuni.smartbudgetapp.web.controllers;


import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.AccountDTO;
import bg.softuni.smartbudgetapp.services.AccountService;
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
@RequestMapping("/users/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService; // за намиране на userId по email

    public AccountController(AccountService accountService,
                             UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping
    public String showAccounts(Model model, Principal principal) {
        // 1. Намираме логнатия user
        String email = principal.getName();
        UserEntity user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Logged user not found!");
        }

        Long userId = user.getId();

        // 2. Всички акаунти за този user
        List<AccountDTO> accounts = accountService.getAllAccountsForUser(userId);
        model.addAttribute("accounts", accounts);

        // 3. Подготвяме празен AccountDTO за формата
        if (!model.containsAttribute("accountDTO")) {
            model.addAttribute("accountDTO", new AccountDTO());
        }

        return "accounts";
    }

    @PostMapping("/create")
    public String createAccount(
            @Valid @ModelAttribute("accountDTO") AccountDTO accountDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) {

        if (bindingResult.hasErrors()) {
            // Има грешки, пак зареждаме accounts.html
            String email = principal.getName();
            UserEntity user = userService.findByEmail(email);
            List<AccountDTO> accounts = accountService.getAllAccountsForUser(user.getId());
            model.addAttribute("accounts", accounts);
            return "accounts";
        }

        // 1. Логнатият user
        String email = principal.getName();
        UserEntity user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Logged user not found!");
        }

        // 2. Създаваме акаунт
        accountService.createAccount(accountDTO, user.getId());

        // 3. Пренасочваме към /users/accounts
        return "redirect:/users/accounts";
    }


}
