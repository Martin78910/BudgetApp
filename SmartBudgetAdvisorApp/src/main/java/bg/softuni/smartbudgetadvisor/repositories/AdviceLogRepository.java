package bg.softuni.smartbudgetadvisor.repositories;

import bg.softuni.smartbudgetadvisor.models.AdviceLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdviceLogRepository extends JpaRepository<AdviceLogEntity, Long> {
    List<AdviceLogEntity> findAllByUserEmail(String userEmail);
}
