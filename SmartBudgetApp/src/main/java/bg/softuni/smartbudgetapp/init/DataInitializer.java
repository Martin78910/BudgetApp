package bg.softuni.smartbudgetapp.init;

import bg.softuni.smartbudgetapp.models.*;
import bg.softuni.smartbudgetapp.repositories.CategoryRepository;
import bg.softuni.smartbudgetapp.repositories.CurrencyRepository;
import bg.softuni.smartbudgetapp.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CurrencyRepository currencyRepository;
    private final CategoryRepository categoryRepository;

    public DataInitializer(RoleRepository roleRepository,
                           CurrencyRepository currencyRepository,
                           CategoryRepository categoryRepository) {
        this.roleRepository = roleRepository;
        this.currencyRepository = currencyRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Зареждаме роли (ако не съществуват)
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_ADMIN");

        // 2. Зареждаме валути (ако не съществуват)
        createCurrencyIfNotExists(CurrencyEnum.BGN);
        createCurrencyIfNotExists(CurrencyEnum.EUR);
        createCurrencyIfNotExists(CurrencyEnum.USD);

        // 3. Зареждаме категории (ако не съществуват)
        createCategoryIfNotExists(CategoryEnum.ЖИЛИЩЕ);
        createCategoryIfNotExists(CategoryEnum.ТРАНСПОРТ);
        createCategoryIfNotExists(CategoryEnum.ХРАНА_И_ДОМАКИНСТВО);
        createCategoryIfNotExists(CategoryEnum.ЗДРАВЕ_И_ЛИЧНИ_ГРИЖИ);
        createCategoryIfNotExists(CategoryEnum.ОБРАЗОВАНИЕ_И_РАЗВИТИЕ);
        createCategoryIfNotExists(CategoryEnum.ДРЕХИ_И_АКСЕСОАРИ);
        createCategoryIfNotExists(CategoryEnum.РАЗВЛЕЧЕНИЯ_И_СВОБОДНО_ВРЕМЕ);
        createCategoryIfNotExists(CategoryEnum.ПОДАРЪЦИ_И_СПЕЦИАЛНИ_СЛУЧАИ);
        createCategoryIfNotExists(CategoryEnum.СПЕСТЯВАНИЯ_И_ИНВЕСТИЦИИ);
        createCategoryIfNotExists(CategoryEnum.ДРУГИ_РАЗХОДИ);
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    RoleEntity newRole = new RoleEntity();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });
    }

    private void createCurrencyIfNotExists(CurrencyEnum currencyEnum) {
        Optional<CurrencyEntity> currencyOpt = currencyRepository.findByCode(currencyEnum);
        if (currencyOpt.isEmpty()) {
            CurrencyEntity currencyEntity = new CurrencyEntity();
            currencyEntity.setCode(currencyEnum);
            currencyRepository.save(currencyEntity);
        }
    }

    private void createCategoryIfNotExists(CategoryEnum categoryEnum) {
        Optional<CategoryEntity> catOpt = categoryRepository.findByCode(categoryEnum);
        if (catOpt.isEmpty()) {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCode(categoryEnum);
            categoryRepository.save(categoryEntity);
        }
    }



}
