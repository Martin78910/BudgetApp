package bg.softuni.smartbudgetadvisor.services.impl;


import bg.softuni.smartbudgetadvisor.models.AdviceLogEntity;
import bg.softuni.smartbudgetadvisor.models.dto.AdviceRequest;
import bg.softuni.smartbudgetadvisor.repositories.AdviceLogRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

public class AdviceServiceImplTest {


    @Test
    public void testGenerateAdviceAndSave() {
        AdviceLogRepository mockRepo = Mockito.mock(AdviceLogRepository.class);
        AdviceServiceImpl service = new AdviceServiceImpl(mockRepo);

        AdviceRequest request = new AdviceRequest();
        request.setBudgetLimit(3000);
        request.setCurrentSpending(1000);

        String advice = service.generateAdviceFor(request);

        assertNotNull(advice);
        assertTrue(advice.contains("200"));
        Mockito.verify(mockRepo, Mockito.times(1)).save(Mockito.any(AdviceLogEntity.class));
    }
}
