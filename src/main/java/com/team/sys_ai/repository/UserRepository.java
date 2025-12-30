package com.team.sys_ai.repository;

import com.team.sys_ai.entity.Role;
import com.team.sys_ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    List<User> findByEntrepotAssigneId(Long entrepotId);

    List<User> findByRole(Role role);

    List<User> findByActifTrue();

    List<User> findByActifTrueAndRole(Role role);

    @Query("SELECT u FROM User u WHERE u.login = :login AND u.actif = true")
    Optional<User> findActiveByLogin(@Param("login") String login);

    long countByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.role = 'GESTIONNAIRE' AND u.entrepotAssigne IS NULL")
    List<User> findGestionnairesWithoutEntrepot();
}
