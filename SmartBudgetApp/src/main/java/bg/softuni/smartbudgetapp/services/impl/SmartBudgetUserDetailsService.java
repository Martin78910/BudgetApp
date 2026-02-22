package bg.softuni.smartbudgetapp.services.impl;


import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SmartBudgetUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SmartBudgetUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Зареждаме потребителя от базата по email (username).
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email " + username + " not found"));

        return mapToUserDetails(userEntity);
    }

    private UserDetails mapToUserDetails(UserEntity userEntity) {
        // Сet<RoleEntity> roles = userEntity.getRoles();
        // Преобразуваме RoleEntity -> SimpleGrantedAuthority("ROLE_...")
        Set<GrantedAuthority> authorities = userEntity
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // "ROLE_USER", "ROLE_ADMIN"
                .collect(Collectors.toUnmodifiableSet());

        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(),
                userEntity.getPassword(),
                authorities
        );
    }






}
