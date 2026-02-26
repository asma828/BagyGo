package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.Role;
import com.bagygo.bagygo_backend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(UserRole name);
}