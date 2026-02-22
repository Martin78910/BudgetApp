package bg.softuni.smartbudgetapp.web.controllers;


import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.AccountDTO;
import bg.softuni.smartbudgetapp.models.dto.BudgetDTO;
import bg.softuni.smartbudgetapp.repositories.CategoryRepository;
import bg.softuni.smartbudgetapp.services.AccountService;
import bg.softuni.smartbudgetapp.services.BudgetService;
import bg.softuni.smartbudgetapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final AccountService accountService;


    public BudgetController(BudgetService budgetService,
                            CategoryRepository categoryRepository, UserService userService, AccountService accountService) {
        this.budgetService = budgetService;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.accountService = accountService;
    }
    @GetMapping
    public String showBudgets(Model model, Principal principal) {
        // 1. currentUserEmail = principal.getName() => връща email
        String currentUserEmail = principal.getName();

        // 2. Намираме UserEntity
        UserEntity user = userService.findByEmail(currentUserEmail);
        if (user == null) {
            throw new RuntimeException("Logged user not found in DB!");
        }

        // 3. Зареждаме списък BudgetDTO за този user
        var budgets = budgetService.getAllBudgetsForUser(user.getId());
        model.addAttribute("budgets", budgets);

        // 4. Празен BudgetDTO + списък категории
        if (!model.containsAttribute("budgetDTO")) {
            model.addAttribute("budgetDTO", new BudgetDTO());
        }
        model.addAttribute("allCategories", categoryRepository.findAll());
        List<AccountDTO> allAccounts = accountService.getAllAccountsForUser(user.getId());
        model.addAttribute("allAccounts", allAccounts);

        return "budgets";
    }

    @PostMapping("/create")
    public String createBudget(
            @Valid @ModelAttribute("budgetDTO") BudgetDTO budgetDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) {

        String currentUserEmail = principal.getName();
        UserEntity user = userService.findByEmail(currentUserEmail);

        if (user == null) {
            throw new RuntimeException("Logged user not found in DB!");
        }

        AccountDTO selectedAccount = accountService.getAccountById(budgetDTO.getAccountId());

        Double totalUsed = budgetService.getTotalBudgetAmountByAccountId(budgetDTO.getAccountId());
        Double available = selectedAccount.getBalance() - totalUsed;

        if (available < budgetDTO.getMonthlyLimit()) {
            bindingResult.rejectValue("accountId", "error.accountId",
                    "Недостатъчен остатък по сметката. Налично: " + available + " лв., изискуемо: "
                            + budgetDTO.getMonthlyLimit() + " лв.");
        }

        if (bindingResult.hasErrors()) {
            var budgets = budgetService.getAllBudgetsForUser(user.getId());
            model.addAttribute("budgets", budgets);
            model.addAttribute("allCategories", categoryRepository.findAll());
            model.addAttribute("allAccounts", accountService.getAllAccountsForUser(user.getId()));
            return "budgets";
        }

        budgetService.createBudget(budgetDTO, user.getId());
        return "redirect:/users/budgets";
    }



}
