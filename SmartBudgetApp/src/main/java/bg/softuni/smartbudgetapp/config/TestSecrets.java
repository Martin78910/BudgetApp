package bg.softuni.smartbudgetapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestSecrets implements CommandLineRunner {

    @Value("${spring.mail.username:NOT_FOUND}")
    private String email;

    @Override
    public void run(String... args) {
        System.out.println("📧 Gmail Username: " + email);
    }

}
