package bg.softuni.smartbudgetadvisor.web.controllers;


import bg.softuni.smartbudgetadvisor.models.dto.AdviceRequest;
import bg.softuni.smartbudgetadvisor.services.AdviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController -> казва, че връща JSON / String от методите
@RestController
@RequestMapping("/api/advice")
public class AdviceController {


    private final AdviceService adviceService;

    public AdviceController(AdviceService adviceService) {
        this.adviceService = adviceService;
    }

    // 1) GET endpoint -> връща "общ" съвет
    @GetMapping
    public ResponseEntity<String> getGenericAdvice() {
        //  "Опитайте да редуцирате своите разходи!"
        String advice = adviceService.generateAdvice();
        return ResponseEntity.ok(advice);
    }

    // 2) POST endpoint -> приема параметри (AdviceRequest), връща персонализиран съвет
    @PostMapping
    public ResponseEntity<String> createAdvice(@RequestBody AdviceRequest request) {
        //  "Добра работа! Остават ви X до лимита" или "Внимание! Превишили сте бюджета"
        String result = adviceService.generateAdviceFor(request);
        return ResponseEntity.ok(result);
    }

    // трети endpoint
    @GetMapping("/test")
    public String testEndpoint() {
        return "Hello from SmartBudgetAdvisor Microservice!";
    }



}
