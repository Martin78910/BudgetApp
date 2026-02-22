package bg.softuni.smartbudgetapp.clients;


import bg.softuni.smartbudgetapp.config.FeignConfig;
import bg.softuni.smartbudgetapp.models.dto.AdviceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(
        name = "advisor-service",
        url = "${advisor.service.url}",
        fallback = AdvisorServiceClientFallback.class,
        configuration = FeignConfig.class
)
public interface AdvisorServiceClient {

    @GetMapping("/api/advice")
    String getGenericAdvice();

    @PostMapping("/api/advice")
    String createPersonalizedAdvice(@RequestBody AdviceDTO adviceDTO);
}