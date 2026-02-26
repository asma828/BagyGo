package com.bagygo.bagygo_backend.config;

import com.bagygo.bagygo_backend.entity.Role;
import com.bagygo.bagygo_backend.enums.UserRole;
import com.bagygo.bagygo_backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize roles if they don't exist
        if (roleRepository.findByName(UserRole.EXPEDITEUR).isEmpty()) {
            Role expediteurRole = Role.builder()
                    .name(UserRole.EXPEDITEUR)
                    .build();
            roleRepository.save(expediteurRole);
        }

        if (roleRepository.findByName(UserRole.TRANSPORTEUR).isEmpty()) {
            Role transporteurRole = Role.builder()
                    .name(UserRole.TRANSPORTEUR)
                    .build();
            roleRepository.save(transporteurRole);
        }
    }
}