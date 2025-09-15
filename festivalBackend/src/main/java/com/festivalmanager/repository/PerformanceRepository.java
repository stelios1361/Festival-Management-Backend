package com.festivalmanager.repository;

import com.festivalmanager.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    boolean existsByName(String name);
}
