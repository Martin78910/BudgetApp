package bg.softuni.smartbudgetapp.services.impl;

import bg.softuni.smartbudgetapp.models.RoleEntity;
import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.UserLoginDTO;
import bg.softuni.smartbudgetapp.repositories.RoleRepository;
import bg.softuni.smartbudgetapp.repositories.UserRepository;
import bg.softuni.smartbudgetapp.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity registerUser(UserEntity user, String role) {
        // Ако не е подадена конкретна роля, ползваме "ROLE_USER" по подразбиране
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }

        // Намираме ролята от базата
        RoleEntity roleEntity = roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(roleEntity);

        // Кодираме първоначалната парола
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity loginUser(UserLoginDTO userLoginDTO) {
        UserEntity user = this.findByEmail(userLoginDTO.getEmail());
        if (user == null) {
            return null; // няма такъв потребител
        }
        boolean matches = passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword());
        if (!matches) {
            return null; // паролата не съвпада
        }
        return user;
    }

    @Override
    public void updateUserProfile(UserEntity updatedUser) {
        // 1. Намираме user от базата
        UserEntity existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Обновяваме полета (име, снимка, валута и т.н.)
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setCurrency(updatedUser.getCurrency());
        existingUser.setProfilePictureUrl(updatedUser.getProfilePictureUrl());

        // Ако искате да позволите смяна на парола през updateProfile,
        // може да проверите дали updatedUser.getPassword() != null && !isBlank()

        userRepository.save(existingUser);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void changeUserRole(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoleEntity roleEntity = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Изчистваме старите роли и добавяме само новата
        user.getRoles().clear();
        user.getRoles().add(roleEntity);

        userRepository.save(user);
    }

}
