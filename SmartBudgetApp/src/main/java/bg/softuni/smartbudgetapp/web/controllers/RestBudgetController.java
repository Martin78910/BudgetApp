package bg.softuni.smartbudgetapp.web.controllers;


import bg.softuni.smartbudgetapp.models.dto.BudgetDTO;
import bg.softuni.smartbudgetapp.services.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class RestBudgetController {

    private final BudgetService budgetService;

    public RestBudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<BudgetDTO>> getBudgetsByAccount(@PathVariable Long accountId) {
        List<BudgetDTO> budgets = budgetService.getBudgetsByAccountId(accountId);
        return ResponseEntity.ok(budgets);
    }
}

