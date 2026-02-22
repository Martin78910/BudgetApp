package bg.softuni.smartbudgetapp.web.controllers;

import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // GET /admin -> зарежда admin.html
    @GetMapping
    public String adminPanel(Model model) {
        List<UserEntity> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "admin";
    }

    // POST /admin/change-role?userId=...&roleName=...
    @PostMapping("/change-role")
    public String changeRole(@RequestParam("userId") Long userId,
                             @RequestParam("roleName") String roleName) {

        // userService има метод changeUserRole(userId, roleName)
        userService.changeUserRole(userId, roleName);
        // след това се връщаме на /admin
        return "redirect:/admin";
    }

}
