package com.bagygo.bagygo_backend.config;

import com.bagygo.bagygo_backend.entity.Role;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.UserRole;
import com.bagygo.bagygo_backend.repository.RoleRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, 
                           UserRepository userRepository, 
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Initialize Roles
        initializeRole(UserRole.EXPEDITEUR);
        initializeRole(UserRole.TRANSPORTEUR);
        initializeRole(UserRole.ADMIN);

        // 2. Initialize Admin User
        initializeAdmin();
    }

    private void initializeRole(UserRole roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            System.out.println("Role initialized: " + roleName);
        }
    }

    private void initializeAdmin() {
        String adminEmail = "admin@bagygo.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .firstName("Platform")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .phone("0600000000")
                    .role(UserRole.ADMIN)
                    .isVerified(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Default Admin account created: " + adminEmail + " / Admin@123");
        }
    }
}