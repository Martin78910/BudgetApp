package bg.softuni.smartbudgetadvisor.services;

import bg.softuni.smartbudgetadvisor.models.dto.AdviceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdviceServiceIntegrationTest {

    @Autowired
    private AdviceService adviceService;

    @Test
    public void testGenerateAdviceWithValidRequest() {
        AdviceRequest request = new AdviceRequest();
        request.setUserEmail("test@example.com");
        request.setBudgetLimit(1500);
        request.setCurrentSpending(1000);

        String result = adviceService.generateAdviceFor(request);
        assertTrue(result.contains("Остават ви"));
    }

}
