package bg.softuni.smartbudgetapp.repositories;

import bg.softuni.smartbudgetapp.models.CategoryEntity;
import bg.softuni.smartbudgetapp.models.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByCode(CategoryEnum code);
}
