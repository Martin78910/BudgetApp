package bg.softuni.smartbudgetapp.repositories;

import bg.softuni.smartbudgetapp.models.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Извлича всички сметки, чийто owner.id съвпада с подадения ownerId
     *
     * @param ownerId ID на собственика
     * @return Списък със сметките на собственика
     */
    List<AccountEntity> findAllByOwnerId(Long ownerId);

    /**
     * Извлича всички сметки, чийто owner.id съвпада с подадения userId
     * (Алтернативен метод със същата функционалност като findAllByOwnerId)
     *
     * @param userId ID на потребителя
     * @return Списък със сметките на потребителя
     */
    List<AccountEntity> findByOwner_Id(Long userId);

    /**
     * Извлича всички сметки, принадлежащи на даден собственик
     *
     * @param owner ID на собственика
     * @return Списък със сметките на собственика
     */
    List<AccountEntity> findAllByOwner_Id(Long owner);

}
