package bg.softuni.smartbudgetapp.services;

public interface EmailService {

    /**
     * Изпраща имейл.
     *
     * @param to      получател
     * @param subject тема на имейла
     * @param body    съдържание на имейла
     */
    void sendEmail(String to, String subject, String body);


}
