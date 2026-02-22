package bg.softuni.smartbudgetadvisor.services;

import bg.softuni.smartbudgetadvisor.models.dto.AdviceRequest;

public interface AdviceService {

    String generateAdvice();
    String generateAdviceFor(AdviceRequest request);


}
