package bg.softuni.smartbudgetapp.scheduler;


import bg.softuni.smartbudgetapp.models.CategoryEnum;
import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.services.BudgetService;
import bg.softuni.smartbudgetapp.services.TransactionService;
import bg.softuni.smartbudgetapp.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@EnableScheduling
public class EmailScheduledTasks {

    // Дефиниране на Logger за този клас
    private static final Logger logger = LoggerFactory.getLogger(EmailScheduledTasks.class);


    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final UserService userService;
    private final JavaMailSender mailSender;

    // Процент от бюджета, при който да изпратим предупреждение (напр. 90%)
    private static final double BUDGET_THRESHOLD_PERCENT = 0.9;

    public EmailScheduledTasks(TransactionService transactionService,
                               BudgetService budgetService,
                               UserService userService,
                               JavaMailSender mailSender) {
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.userService = userService;
        this.mailSender = mailSender;

        logger.info("EmailScheduledTasks е инициализиран.");
    }

    /**
     * Автоматичен месечен отчет – всяко 1-во число в 08:00
     */
   @Scheduled(cron = "0 0 8 1 * ?")
//    @Scheduled(cron = "0 * * * * ?")  // Изпълнява се всяка минута
    public void sendMonthlyFinancialReport() {
        logger.info(">>> sendMonthlyFinancialReport() стартира!");

        // Намираме всички потребители
        List<UserEntity> allUsers = userService.findAllUsers();
        if (allUsers.isEmpty()) {
            logger.warn("Няма намерени потребители за изпращане на отчет.");
        } else {
            logger.info("Намерени са {} потребители.", allUsers.size());
        }

        // Предишен месец
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();

        // За всеки user - събираме данни и пращаме email
        for (UserEntity user : allUsers) {
            // Примерна, опростена логика за общи разходи:
            double totalExpenses = 0.0;
            double totalIncome = 0.0;


            //  сумираме разходите за всички категории (без филтър по дати).
            for (CategoryEnum cat : CategoryEnum.values()) {
                totalExpenses += transactionService.getTotalSpendingForCategory(user.getId(), cat);
            }

            // Подготвяме тема и текст
            String subject = "Месечен отчет за " + previousMonth.getMonth() + " " + previousMonth.getYear();
            StringBuilder sb = new StringBuilder();
            sb.append("Здравей, ").append(user.getEmail()).append("!\n\n")
                    .append("Ето твоят месечен финансов отчет за ")
                    .append(previousMonth.getMonth()).append(" ")
                    .append(previousMonth.getYear()).append(":\n")
                    .append(" - Общо разходи: ").append(totalExpenses).append(" лв.\n")
                    .append(" - Общо приходи: ").append(totalIncome).append(" лв.\n")
                    .append("=================================\n")
                    .append("Поздрави,\nSmartBudgetApp");

            try {
                sendEmail(user.getEmail(), subject, sb.toString());
                logger.info("Изпратен отчет до: {}", user.getEmail());
            } catch (MessagingException e) {
                logger.error("Грешка при изпращане на имейл до {}: {}", user.getEmail(), e.getMessage());
            }
        }
        logger.info("Завърши изпращане на месечен финансов отчет...");
    }

    /**
     * Ежедневна проверка на изпълнението на бюджета – всеки ден в 10:00
     */
  @Scheduled(cron = "0 0 10 * * ?")
//  @Scheduled(cron = "0 * * * * ?")  // Изпълнява се всяка минута
    public void checkBudgetExecution() {
        logger.info("Започва ежедневна проверка на бюджета...");
        List<UserEntity> allUsers = userService.findAllUsers();

        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        for (UserEntity user : allUsers) {
            for (CategoryEnum cat : CategoryEnum.values()) {
                double monthlyLimit = budgetService.getMonthlyLimit(user.getId(), cat);
                if (monthlyLimit <= 0) {
                    continue; // няма бюджет за тази категория
                }

                // getTotalSpendingForCategory -> общите разходи (income=false) за конкретната категория
                double spentSoFar = transactionService.getTotalSpendingForCategory(user.getId(), cat);

                double threshold = monthlyLimit * BUDGET_THRESHOLD_PERCENT;

                if (spentSoFar >= threshold) {
                    String subject = "Предупреждение за бюджет в категория " + cat;
                    String text = String.format("Здравей, %s!%n", user.getEmail()) +
                            String.format("Вече си изразходвал над %.0f%% от бюджета за %s.%n",
                                    BUDGET_THRESHOLD_PERCENT * 100, cat) +
                            String.format("Лимит: %.2f лв., Похарчено: %.2f лв.%n", monthlyLimit, spentSoFar) +
                            "=============================\n" +
                            "SmartBudgetApp";
                    try {
                        sendEmail(user.getEmail(), subject, text);
                    } catch (MessagingException e) {
                        logger.error("Грешка при изпращане на имейл до {}: {}", user.getEmail(), e.getMessage());
                    }
                }
            }
        }
        logger.info("Завърши ежедневната проверка на бюджета.");
    }

    /**
     *  метод за изпращане на имейл
     */
    private void sendEmail(String recipientEmail, String subject, String text) throws MessagingException {
        logger.info("Изпращане на имейл до {} със заглавие '{}'", recipientEmail, subject);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(text);

        mailSender.send(mimeMessage);

        logger.info("Имейлът до {} беше изпратен успешно.", recipientEmail);
    }

}
