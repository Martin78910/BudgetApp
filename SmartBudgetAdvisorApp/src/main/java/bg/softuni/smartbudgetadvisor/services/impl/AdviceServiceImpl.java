package bg.softuni.smartbudgetadvisor.services.impl;

import bg.softuni.smartbudgetadvisor.models.AdviceLogEntity;
import bg.softuni.smartbudgetadvisor.models.dto.AdviceRequest;
import bg.softuni.smartbudgetadvisor.repositories.AdviceLogRepository;
import bg.softuni.smartbudgetadvisor.services.AdviceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdviceServiceImpl implements AdviceService {


    private final AdviceLogRepository adviceLogRepository;

    public AdviceServiceImpl(AdviceLogRepository adviceLogRepository) {
        this.adviceLogRepository = adviceLogRepository;
    }

    @Override
    public String generateAdvice() {
        // Примерен "общ" съвет
        String text = "Опитайте да редуцирате своите разходи!";

        // По желание може да запишем в базата, напр. userEmail = "anonymous"
        AdviceLogEntity logEntity = new AdviceLogEntity();
        logEntity.setUserEmail("anonymous");
        logEntity.setAdviceText(text);
        logEntity.setCreatedOn(LocalDateTime.now());
        adviceLogRepository.save(logEntity);

        return text;
    }

    @Override
    public String generateAdviceFor(AdviceRequest request) {
        // Логика спрямо request
        double diff = request.getBudgetLimit() - request.getCurrentSpending();
        String result;
        if (diff < 0) {
            result = "Внимание! Превишили сте бюджета си!";
        } else {
            result = "Добра работа! Остават ви " + diff + " до лимита.";
        }

        // Създаваме и запазваме в DB
        AdviceLogEntity logEntity = new AdviceLogEntity();
        logEntity.setUserEmail(request.getUserEmail());
        logEntity.setCurrentSpending(request.getCurrentSpending());
        logEntity.setBudgetLimit(request.getBudgetLimit());
        logEntity.setAdviceText(result);
        logEntity.setCreatedOn(LocalDateTime.now());
        adviceLogRepository.save(logEntity);

        return result;
    }


}
