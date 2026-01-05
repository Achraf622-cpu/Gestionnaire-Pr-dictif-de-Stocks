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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EntrepotRepository entrepotRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users (non-paginated for backward compatibility).
     */
    public List<UserDTO> getAllUsers() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    /**
     * Get all users (paginated).
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    /**
     * Get active users (non-paginated).
     */
    public List<UserDTO> getActiveUsers() {
        return userMapper.toDTOList(userRepository.findByActifTrue());
    }

    /**
     * Get active users (paginated).
     */
    public Page<UserDTO> getActiveUsers(Pageable pageable) {
        return userRepository.findByActifTrue(pageable)
                .map(userMapper::toDTO);
    }

    /**
     * Get users by role (non-paginated).
     */
    public List<UserDTO> getUsersByRole(Role role) {
        return userMapper.toDTOList(userRepository.findByRole(role));
    }

    /**
     * Get users by role (paginated).
     */
    public Page<UserDTO> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(userMapper::toDTO);
    }

    public UserDTO getUserById(Long id) {
        User user = findById(id);
        return userMapper.toDTO(user);
    }

    public List<UserDTO> getGestionnairesByEntrepot(Long entrepotId) {
        return userMapper.toDTOList(userRepository.findByEntrepotAssigneId(entrepotId));
    }

    @Transactional
    public UserDTO createUser(UserCreateDTO dto) {
        if (userRepository.existsByLogin(dto.getLogin())) {
            throw new BusinessValidationException("login", "Ce login est déjà utilisé");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessValidationException("email", "Cet email est déjà utilisé");
        }

        validateRoleAndWarehouse(dto.getRole(), dto.getEntrepotAssigneId());

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActif(true);

        if (dto.getRole() == Role.GESTIONNAIRE && dto.getEntrepotAssigneId() != null) {
            Entrepot entrepot = entrepotRepository.findById(dto.getEntrepotAssigneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Entrepôt", "id", dto.getEntrepotAssigneId()));
            user.setEntrepotAssigne(entrepot);
        }

        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = findById(id);
        userRepository.findByLogin(dto.getLogin())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new BusinessValidationException("login", "Ce login est déjà utilisé");
                });

        userRepository.findByEmail(dto.getEmail())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new BusinessValidationException("email", "Cet email est déjà utilisé");
                });

        validateRoleAndWarehouse(dto.getRole(), dto.getEntrepotAssigneId());

        userMapper.updateEntity(dto, user);

        if (dto.getRole() == Role.GESTIONNAIRE && dto.getEntrepotAssigneId() != null) {
            Entrepot entrepot = entrepotRepository.findById(dto.getEntrepotAssigneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Entrepôt", "id", dto.getEntrepotAssigneId()));
            user.setEntrepotAssigne(entrepot);
        } else if (dto.getRole() == Role.ADMIN) {
            user.setEntrepotAssigne(null);
        }

        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public UserDTO assignToEntrepot(Long userId, Long entrepotId) {
        User user = findById(userId);

        if (user.getRole() != Role.GESTIONNAIRE) {
            throw new BusinessValidationException("Seuls les gestionnaires peuvent être assignés à un entrepôt");
        }

        Entrepot entrepot = entrepotRepository.findById(entrepotId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt", "id", entrepotId));

        user.setEntrepotAssigne(entrepot);
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = findById(id);
        user.setActif(false);
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO activateUser(Long id) {
        User user = findById(id);
        user.setActif(true);
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private void validateRoleAndWarehouse(Role role, Long entrepotId) {
        if (role == Role.GESTIONNAIRE && entrepotId == null) {
            throw new BusinessValidationException("entrepotAssigneId",
                    "Un gestionnaire doit être assigné à un entrepôt");
        }
        if (role == Role.ADMIN && entrepotId != null) {
            throw new BusinessValidationException("entrepotAssigneId",
                    "Un administrateur ne peut pas être assigné à un entrepôt spécifique");
        }
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
    }
}
