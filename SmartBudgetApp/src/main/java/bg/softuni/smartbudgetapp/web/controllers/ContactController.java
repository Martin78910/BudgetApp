package bg.softuni.smartbudgetapp.web.controllers;


import bg.softuni.smartbudgetapp.models.dto.ContactFormDTO;
import bg.softuni.smartbudgetapp.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    private final EmailService emailService;


    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }


    @GetMapping("/contact")
    public String showContactForm(Model model) {
        if (!model.containsAttribute("contactFormDto")) {
            model.addAttribute("contactFormDto", new ContactFormDTO());
        }
        return "contact";
    }

    @PostMapping("/contact/send")
    public String sendContactMessage(
            @ModelAttribute("contactFormDto") @Valid ContactFormDTO contactForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactFormDto", bindingResult);
            redirectAttributes.addFlashAttribute("contactFormDto", contactForm);
            return "redirect:/contact";
        }

        String subject = "Контактна форма от " + contactForm.getSenderName();
        String body = "Име: " + contactForm.getSenderName() +
                "\nИмейл: " + contactForm.getSenderEmail() +
                "\n\nСъобщение:\n" + contactForm.getMessage();

        emailService.sendEmail("newsmartbudgetapp@gmail.com", subject, body);
        return "redirect:/contact?success";
    }


}
