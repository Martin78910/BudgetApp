package bg.softuni.smartbudgetapp.services;

import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.UserLoginDTO;

import java.util.List;

public interface UserService {

    // Регистриране (поне с "ROLE_USER")
    UserEntity registerUser(UserEntity user, String role);

    // Намиране по email
    UserEntity findByEmail(String email);

    // Обновяване на профила (име, снимка, и т.н.)
    void updateUserProfile(UserEntity user);

    // Всички потребители (за админ панел)
    List<UserEntity> getAllUsers();

    // Смяна на роля (user <-> admin)
    void changeUserRole(Long userId, String roleName);

    // Метод, който ще връща всички потребители
    List<UserEntity> findAllUsers();

    // Логика за логин
    UserEntity loginUser(UserLoginDTO userLoginDTO);
}
