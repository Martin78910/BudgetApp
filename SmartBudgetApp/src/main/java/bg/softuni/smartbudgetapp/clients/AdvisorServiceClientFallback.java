package bg.softuni.smartbudgetapp.clients;

import bg.softuni.smartbudgetapp.models.dto.AdviceDTO;
import org.springframework.stereotype.Component;


@Component
public class AdvisorServiceClientFallback implements AdvisorServiceClient {

    @Override
    public String getGenericAdvice() {
        return "Съветът не е наличен в момента. Опитайте по-късно.";
    }

    @Override
    public String createPersonalizedAdvice(AdviceDTO adviceDTO) {
        return "Неуспешно изпращане на съвет. Опитайте по-късно.";
    }


}
