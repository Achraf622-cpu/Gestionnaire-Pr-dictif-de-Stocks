package com.team.sys_ai.service;

import com.team.sys_ai.dto.UserCreateDTO;
import com.team.sys_ai.dto.UserDTO;
import com.team.sys_ai.entity.Entrepot;
import com.team.sys_ai.entity.Role;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.exception.BusinessValidationException;
import com.team.sys_ai.exception.ResourceNotFoundException;
import com.team.sys_ai.mapper.UserMapper;
import com.team.sys_ai.repository.EntrepotRepository;
import com.team.sys_ai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntrepotRepository entrepotRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User gestionnaireUser;
    private Entrepot entrepot;
    private UserDTO userDTO;
    private UserCreateDTO userCreateDTO;

    @BeforeEach
    void setUp() {
        entrepot = Entrepot.builder()
                .id(1L)
                .nom("Entrepôt Test")
                .ville("Paris")
                .actif(true)
                .build();

        adminUser = User.builder()
                .id(1L)
                .login("admin")
                .password("hashedPassword")
                .nom("Admin")
                .prenom("User")
                .email("admin@test.com")
                .role(Role.ADMIN)
                .actif(true)
                .build();

        gestionnaireUser = User.builder()
                .id(2L)
                .login("gestionnaire")
                .password("hashedPassword")
                .nom("Gestionnaire")
                .prenom("User")
                .email("gestionnaire@test.com")
                .role(Role.GESTIONNAIRE)
                .actif(true)
                .entrepotAssigne(entrepot)
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .login("admin")
                .nom("Admin")
                .prenom("User")
                .email("admin@test.com")
                .role(Role.ADMIN)
                .actif(true)
                .build();

        userCreateDTO = UserCreateDTO.builder()
                .login("newuser")
                .password("password123")
                .nom("New")
                .prenom("User")
                .email("new@test.com")
                .role(Role.GESTIONNAIRE)
                .entrepotAssigneId(1L)
                .build();
    }

    @Test
    @DisplayName("Should return all users")
    void getAllUsers_ReturnsAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of(adminUser, gestionnaireUser));
        when(userMapper.toDTOList(any())).thenReturn(List.of(userDTO));

        // When
        List<UserDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotEmpty();
        verify(userRepository).findAll();
        verify(userMapper).toDTOList(any());
    }

    @Test
    @DisplayName("Should return user by ID")
    void getUserById_ExistingId_ReturnsUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userMapper.toDTO(adminUser)).thenReturn(userDTO);

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLogin()).isEqualTo("admin");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getUserById_NonExistingId_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Utilisateur");
    }

    @Test
    @DisplayName("Should create new user with hashed password")
    void createUser_ValidData_CreatesUser() {
        // Given
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(entrepotRepository.findById(1L)).thenReturn(Optional.of(entrepot));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userMapper.toEntity(userCreateDTO)).thenReturn(gestionnaireUser);
        when(userRepository.save(any(User.class))).thenReturn(gestionnaireUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // When
        UserDTO result = userService.createUser(userCreateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when login already exists")
    void createUser_DuplicateLogin_ThrowsException() {
        // Given
        when(userRepository.existsByLogin("newuser")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(userCreateDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("login");
    }

    @Test
    @DisplayName("Should throw exception when creating GESTIONNAIRE without entrepot")
    void createUser_GestionnaireWithoutEntrepot_ThrowsException() {
        // Given
        userCreateDTO.setEntrepotAssigneId(null);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(userCreateDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("entrepôt");
    }

    @Test
    @DisplayName("Should deactivate user")
    void deactivateUser_ExistingUser_DeactivatesUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // When
        UserDTO result = userService.deactivateUser(1L);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(argThat(user -> !user.getActif()));
    }

    @Test
    @DisplayName("Should assign gestionnaire to entrepot")
    void assignToEntrepot_ValidGestionnaire_AssignsToEntrepot() {
        // Given
        gestionnaireUser.setEntrepotAssigne(null);
        when(userRepository.findById(2L)).thenReturn(Optional.of(gestionnaireUser));
        when(entrepotRepository.findById(1L)).thenReturn(Optional.of(entrepot));
        when(userRepository.save(any(User.class))).thenReturn(gestionnaireUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // When
        UserDTO result = userService.assignToEntrepot(2L, 1L);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(argThat(user -> user.getEntrepotAssigne() != null));
    }

    @Test
    @DisplayName("Should throw exception when assigning admin to entrepot")
    void assignToEntrepot_AdminUser_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // When/Then
        assertThatThrownBy(() -> userService.assignToEntrepot(1L, 1L))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("gestionnaires");
    }
}
