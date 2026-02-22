package bg.softuni.smartbudgetapp.web.controllers;


import bg.softuni.smartbudgetapp.clients.AdvisorServiceClient;
import bg.softuni.smartbudgetapp.models.dto.AdviceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
@RestController
@RequestMapping("/api/advice")
public class AdviceRestController {
    private final AdvisorServiceClient advisorServiceClient;

    public AdviceRestController(AdvisorServiceClient advisorServiceClient) {
        this.advisorServiceClient = advisorServiceClient;
    }

    @GetMapping
    public ResponseEntity<String> getAdvice() {
        String response = advisorServiceClient.getGenericAdvice();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<String> postAdvice(@RequestBody AdviceDTO adviceDTO) {
        String response = advisorServiceClient.createPersonalizedAdvice(adviceDTO);
        return ResponseEntity.ok(response);
    }
}