package bg.softuni.smartbudgetapp.web.controllers;

import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.BudgetReportDTO;
import bg.softuni.smartbudgetapp.services.AccountService;
import bg.softuni.smartbudgetapp.services.TransactionService;
import bg.softuni.smartbudgetapp.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Контролер за генериране и показване на отчети за бюджета.
 * Предоставя графично представяне на разходите по категории.
 */
@Controller
@RequestMapping("/users/budget-report")
public class BudgetReportController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final AccountService accountService;

    public BudgetReportController(TransactionService transactionService,
                                  UserService userService,
                                  AccountService accountService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.accountService = accountService;
    }

    /**
     * Показва страница с отчет за бюджета под формата на кръгова диаграма.
     *
     * @param month Незадължителен параметър за месец (по подразбиране текущия месец)
     * @param year Незадължителен параметър за година (по подразбиране текущата година)
     * @param model Spring MVC модел
     * @param principal Информация за текущия потребител
     * @return Име на изгледа за отчет на бюджета
     */
    @GetMapping
    public String showBudgetReport(
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "year", required = false) Integer year,
            Model model,
            Principal principal) {

        // Ако месецът не е посочен, използваме текущия
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }

        // Ако годината не е посочена, използваме текущата
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        // Извличаме потребителя
        String email = principal.getName();
        UserEntity user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Потребителят не е намерен!");
        }

        // Извличаме данните за отчета от сървиса
        List<BudgetReportDTO> reportData = transactionService.getBudgetReportData(user.getId(), month, year);

        // Добавяме име на месеца за заглавие
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        LocalDate date = LocalDate.of(year, month, 1);
        String formattedDate = date.format(formatter);

        // Добавяме данните в модела
        model.addAttribute("reportData", reportData);
        model.addAttribute("currentMonth", formattedDate);
        model.addAttribute("currentYear", year);

        // Получаваме списък с всички сметки на потребителя за формата
        model.addAttribute("accounts", accountService.getAllAccountsForUser(user.getId()));

        // Изчисляваме общата сума на разходите
        double totalExpenses = reportData.stream()
                .mapToDouble(BudgetReportDTO::getAmount)
                .sum();
        model.addAttribute("totalExpenses", totalExpenses);

        return "budget-report";
    }




}
