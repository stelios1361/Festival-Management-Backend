package com.festivalmanager.repository;

import com.festivalmanager.model.Festival;
import com.festivalmanager.model.Festival.FestivalState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
    Optional<Festival> findByName(String name);
    boolean existsByName(String name);
    List<Festival> findAllByState(FestivalState state);
}
