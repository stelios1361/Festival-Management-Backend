package com.festivalmanager.repository;

import com.festivalmanager.model.Festival;
import com.festivalmanager.model.Performance;
import com.festivalmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    boolean existsByName(String name);

    boolean existsByIdAndCreator(Long performanceId, User creator);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END "
            + "FROM Performance p "
            + "WHERE p.festival = :festival AND LOWER(p.name) = LOWER(:name) AND p.id <> :excludeId")
    boolean existsByFestivalAndNameExcludingPerformance(@Param("festival") Festival festival,
            @Param("name") String name,
            @Param("excludeId") Long excludeId);

}
