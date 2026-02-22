package bg.softuni.smartbudgetapp.repositories;

import bg.softuni.smartbudgetapp.models.CurrencyEntity;
import bg.softuni.smartbudgetapp.models.CurrencyEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
    Optional<CurrencyEntity> findByCode(CurrencyEnum code);

}
