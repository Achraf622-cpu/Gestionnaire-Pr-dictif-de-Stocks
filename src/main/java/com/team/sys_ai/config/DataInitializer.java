package com.team.sys_ai.config;

import com.team.sys_ai.entity.Entrepot;
import com.team.sys_ai.entity.Role;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.repository.EntrepotRepository;
import com.team.sys_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializes the database with sample data for development/testing.
 * Only runs when the database is empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EntrepotRepository entrepotRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized. Skipping data initialization.");
            return;
        }

        log.info("Initializing database with sample data...");


        Entrepot entrepotParis = createEntrepot("Entrepôt Paris", "12 Rue de la Logistique", "Paris", "75001");
        Entrepot entrepotLyon = createEntrepot("Entrepôt Lyon", "45 Avenue du Transport", "Lyon", "69001");
        Entrepot entrepotMarseille = createEntrepot("Entrepôt Marseille", "78 Boulevard Maritime", "Marseille",
                "13001");

        // Create 1 Admin
        createUser("admin", "admin123", "Dupont", "Jean", "admin@sysai.com", Role.ADMIN, null);


        createUser("gestionnaire1", "gest123", "Martin", "Pierre", "pierre.martin@sysai.com", Role.GESTIONNAIRE,
                entrepotParis);
        createUser("gestionnaire2", "gest123", "Bernard", "Marie", "marie.bernard@sysai.com", Role.GESTIONNAIRE,
                entrepotLyon);
        createUser("gestionnaire3", "gest123", "Petit", "Luc", "luc.petit@sysai.com", Role.GESTIONNAIRE,
                entrepotMarseille);

        log.info("Database initialization completed successfully!");
        log.info("===========================================");
        log.info("Sample users created:");
        log.info("  ADMIN: admin / admin123");
        log.info("  GESTIONNAIRE: gestionnaire1 / gest123 (Paris)");
        log.info("  GESTIONNAIRE: gestionnaire2 / gest123 (Lyon)");
        log.info("  GESTIONNAIRE: gestionnaire3 / gest123 (Marseille)");
        log.info("===========================================");
    }

    private Entrepot createEntrepot(String nom, String adresse, String ville, String codePostal) {
        Entrepot entrepot = Entrepot.builder()
                .nom(nom)
                .adresse(adresse)
                .ville(ville)
                .codePostal(codePostal)
                .actif(true)
                .build();
        entrepot = entrepotRepository.save(entrepot);
        log.info("Created entrepot: {} in {}", nom, ville);
        return entrepot;
    }

    private User createUser(String login, String password, String nom, String prenom,
            String email, Role role, Entrepot entrepot) {
        User user = User.builder()
                .login(login)
                .password(passwordEncoder.encode(password))
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .role(role)
                .actif(true)
                .entrepotAssigne(entrepot)
                .build();
        user = userRepository.save(user);
        log.info("Created user: {} with role {} {}", login, role,
                entrepot != null ? "(assigned to " + entrepot.getNom() + ")" : "");
        return user;
    }
}
