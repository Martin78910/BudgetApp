package bg.softuni.smartbudgetapp.services;

import bg.softuni.smartbudgetapp.models.RoleEntity;
import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.UserLoginDTO;
import bg.softuni.smartbudgetapp.repositories.RoleRepository;
import bg.softuni.smartbudgetapp.repositories.UserRepository;
import bg.softuni.smartbudgetapp.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("plainPassword");
    }

    @Test
    void testRegisterUser_withValidRole() {
        RoleEntity role = new RoleEntity();
        role.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userService.registerUser(testUser, "ROLE_USER");

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository).save(result);
    }

    @Test
    void testRegisterUser_withMissingRole_usesDefault() {
        RoleEntity defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userService.registerUser(testUser, null);

        assertNotNull(result);
        assertEquals("encoded", result.getPassword());
        verify(roleRepository).findByName("ROLE_USER");
    }

    @Test
    void testRegisterUser_roleNotFound_throwsException() {
        when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerUser(testUser, "NON_EXISTENT_ROLE"));

        assertEquals("Role not found", exception.getMessage());
    }

    @Test
    void testFindByEmail_returnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserEntity result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }


    @Test
    void testGetAllUsers_returnsUserList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserEntity> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());
    }

    @Test
    void testChangeUserRole_updatesRole() {
        RoleEntity newRole = new RoleEntity();
        newRole.setName("ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(newRole));

        userService.changeUserRole(1L, "ROLE_ADMIN");

        verify(userRepository).save(testUser);
        assertEquals(1, testUser.getRoles().size());
    }

    @Test
    void testFindAllUsers_returnsUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<UserEntity> result = userService.findAllUsers();

        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void testLoginUser_success() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("plainPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("plainPassword", "plainPassword")).thenReturn(true);

        UserEntity result = userService.loginUser(loginDTO);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testLoginUser_wrongPassword_returnsNull() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "plainPassword")).thenReturn(false);

        UserEntity result = userService.loginUser(loginDTO);

        assertNull(result);
    }

    @Test
    void testLoginUser_userNotFound_returnsNull() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UserEntity result = userService.loginUser(loginDTO);

        assertNull(result);
    }
}

