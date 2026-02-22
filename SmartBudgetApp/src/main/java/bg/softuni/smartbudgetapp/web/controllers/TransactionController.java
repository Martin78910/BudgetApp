package bg.softuni.smartbudgetapp.web.controllers;

import bg.softuni.smartbudgetapp.clients.AdvisorServiceClient;
import bg.softuni.smartbudgetapp.models.CategoryEnum;
import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.AccountDTO;
import bg.softuni.smartbudgetapp.models.dto.AdviceDTO;
import bg.softuni.smartbudgetapp.models.dto.TransactionDTO;
import bg.softuni.smartbudgetapp.services.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/**
 * Контролер за управление на транзакциите в приложението Smart Budget.
 * Обработва показването, добавянето и валидирането на транзакции за потребителски сметки.
 */
@Controller
@RequestMapping("/users/transactions")
public class TransactionController {

    // Сървис зависимости за управление на транзакциите
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final UserService userService;
    private final BudgetService budgetService;
    private final AdvisorServiceClient advisorServiceClient;


    /**
     * Конструктор за инжектиране на необходимите сървиси.
     */
    public TransactionController(TransactionService transactionService,
                                 CategoryService categoryService,
                                 AccountService accountService,
                                 UserService userService,
                                 BudgetService budgetService,
                                 AdvisorServiceClient advisorServiceClient) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.userService = userService;
        this.budgetService = budgetService;
        this.advisorServiceClient = advisorServiceClient;
    }

    /**
     * Обработва GET заявка за показване на транзакции.
     *
     * @param accountId Незадължителен идентификатор на сметка за филтриране на транзакциите
     * @param modelSpring MVC модел за предаване на данни към изгледа
     * @param principal Информация за автентикация на текущия потребител
     * @return Име на изгледа за страницата с транзакции
     */
    @GetMapping
    public String showTransactions(@RequestParam(name = "accountId", required = false) Long accountId,
                                   Model model,
                                   Principal principal) {

        // Извличане на влезлия потребител по имейл
        String email = principal.getName();
        UserEntity user = userService.findByEmail(email);

        // Инициализиране на нов DTO за транзакция, ако не съществува
        if (!model.containsAttribute("transactionDTO")) {
            model.addAttribute("transactionDTO", new TransactionDTO());
        }

        // Хвърляне на изключение, ако потребителят не е намерен
        if (user == null) {
            throw new RuntimeException("Потребителят не е намерен!");
        }


        // Зареждане на сметките на потребителя
        List<AccountDTO> userAccounts = accountService.getAllAccountsForUser(user.getId());
        model.addAttribute("accounts", userAccounts);

        List<TransactionDTO> transactions;
        TransactionDTO transactionDTO = new TransactionDTO();

        // Филтриране на транзакциите по избрана сметка
        if (accountId != null) {
            transactions = transactionService.getTransactionsByAccountId(accountId);
            transactionDTO.setAccountId(accountId);
            model.addAttribute("selectedAccountId", accountId);

            // Получаваме само категориите, за които има бюджет за избраната сметка
            List<CategoryEnum> availableCategories = budgetService.getActiveCategoriesForAccount(accountId);
            model.addAttribute("availableCategories", availableCategories);
        } else {
            transactions = List.of();
            model.addAttribute("availableCategories", List.of());
        }

        // Добавяне на атрибути за визуализация
        model.addAttribute("transactions", transactions);
        model.addAttribute("transactionDTO", transactionDTO);
        model.addAttribute("allCategories", categoryService.getAllCategories());

        return "transactions";
    }

    /**
     * Обработва POST заявка за добавяне на нова транзакция.
     *
     * @param transactionDTO Обект за пренос на данни за транзакция
     * @param bindingResult Резултат от валидацията
     * @param model Spring MVC модел
     * @param principal Информация за автентикация на текущия потребител
     * @param redirectAttrsAttributes за пренасочване с флаш съобщения
     * @return Пренасочване към страницата с транзакции или връщане към формата с грешки
     */
    @PostMapping("/add")
    public String addTransaction(@Valid @ModelAttribute("transactionDTO") TransactionDTO transactionDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 Principal principal,
                                 RedirectAttributes redirectAttrs) {

        // Извличане на влезлия потребител
        String email = principal.getName();
        UserEntity user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Потребителят не е намерен!");
        }

        Long accountId = transactionDTO.getAccountId();
        var category = transactionDTO.getCategory();
        boolean budgetExceeded = false;

        // Допълнителна валидация за разходни транзакции
        if (!transactionDTO.isIncome()) {
            // Извличане на месечния лимит за категорията и сметката
            Double monthlyLimit = budgetService.getLimitByAccountIdAndCategory(accountId, category);

            if (monthlyLimit != null && monthlyLimit > 0) {
                // Изчисляване на досегашните разходи за категорията
                double spentSoFar = transactionService.getTotalExpensesByAccountAndCategory(accountId, category);

                // Изчисляване на новия общ разход
                double newTotalSpent = spentSoFar + transactionDTO.getAmount();

                // Спиране на транзакцията, ако надвишава бюджета
                if (newTotalSpent > monthlyLimit) {
                    budgetExceeded = true;

                    // Предоставяне на специфично съобщение за оставащия бюджет
                    bindingResult.rejectValue("amount", "error.amount",
                            "Надвишен бюджет за категория " + category +
                                    ". Налични средства: " + String.format("%.2f", (monthlyLimit - spentSoFar)));

                    // Извикване на микросървиса за съвет при надвишен бюджет
                    String genericAdvice = advisorServiceClient.getGenericAdvice();
                    model.addAttribute("genericAdviceMessage", genericAdvice);

                    // Възстановяване на модела с текущото състояние
                    model.addAttribute("transactions", transactionService.getTransactionsByAccountId(accountId));
                    model.addAttribute("transactionDTO", transactionDTO);
                    model.addAttribute("accounts", accountService.getAllAccountsForUser(user.getId()));
                    model.addAttribute("selectedAccountId", accountId);
                    model.addAttribute("allCategories", categoryService.getAllCategories());
                    model.addAttribute("availableCategories", budgetService.getActiveCategoriesForAccount(accountId));
                    return "transactions";
                }

                // Извикване на микросървис при достигане на 90% от бюджета
                double spentPercentage = (newTotalSpent / monthlyLimit) * 100;
                if (spentPercentage >= 90) {
                    // Подготвяне на DTO с информация за разходите
                    AdviceDTO adviceDTO = new AdviceDTO();
                    adviceDTO.setUserEmail(user.getEmail());
                    adviceDTO.setCurrentSpending(newTotalSpent);
                    adviceDTO.setBudgetLimit(monthlyLimit);

                    // Извикване на външен микросървис за персонализиран съвет
                    String adviceMessage = advisorServiceClient.createPersonalizedAdvice(adviceDTO);

                    // Извикваме общия съвет от микросървиса
                    String genericAdvice = advisorServiceClient.getGenericAdvice();
                    redirectAttrs.addFlashAttribute("genericAdviceMessage", genericAdvice);

                    // Добавяне на съобщението за визуализация
                    redirectAttrs.addFlashAttribute("adviceMessage", adviceMessage);
                }
            }
        }

        // Връщане към формата, ако има грешки при валидацията
        if (bindingResult.hasErrors()) {
            // Извикваме общия съвет от микросървиса само когато има валидационни грешки
            if (!budgetExceeded) { // Избягваме дублиране ако вече е извикан
                String genericAdvice = advisorServiceClient.getGenericAdvice();
                model.addAttribute("genericAdviceMessage", genericAdvice);
            }

            // Допълване на модела с нужните данни
            model.addAttribute("accounts", accountService.getAllAccountsForUser(user.getId()));
            model.addAttribute("allCategories", categoryService.getAllCategories());
            if (accountId != null) {
                model.addAttribute("transactions", transactionService.getTransactionsByAccountId(accountId));
                model.addAttribute("availableCategories", budgetService.getActiveCategoriesForAccount(accountId));
                model.addAttribute("selectedAccountId", accountId);
            } else {
                model.addAttribute("transactions", List.of());
                model.addAttribute("availableCategories", List.of());
            }
            return "transactions";
        }

        // Запазване на транзакцията
        TransactionDTO savedTx = transactionService.addTransaction(transactionDTO);

        // Пренасочване към списъка с транзакции за конкретната сметка
        return "redirect:/users/transactions?accountId=" + savedTx.getAccountId();
    }
}