package bg.softuni.smartbudgetapp.web.controllers;

import bg.softuni.smartbudgetapp.models.dto.ContactFormDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {
    @GetMapping("/about")
    public String about() {
        return "about";
    }



}
