package com.example.admin.repository;

import com.example.admin.model.Role;
import com.example.admin.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(RoleType name);
}
