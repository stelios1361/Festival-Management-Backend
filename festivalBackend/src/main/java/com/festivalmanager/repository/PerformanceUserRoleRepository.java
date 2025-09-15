package com.festivalmanager.repository;

import com.festivalmanager.model.PerformanceUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceUserRoleRepository extends JpaRepository<PerformanceUserRole, Long> {
    // You can add custom queries here if needed
}
