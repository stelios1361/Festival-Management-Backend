package com.festivalmanager.repository;

import com.festivalmanager.enums.FestivalRoleType;
import com.festivalmanager.model.FestivalUserRole;
import com.festivalmanager.model.Festival;
import com.festivalmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FestivalUserRoleRepository extends JpaRepository<FestivalUserRole, Long> {

    List<FestivalUserRole> findByFestival(Festival festival);

    List<FestivalUserRole> findByUser(User user);

    Optional<FestivalUserRole> findByFestivalAndUser(Festival festival, User user);

    boolean existsByFestivalAndUserAndRole(Festival festival, User user, FestivalRoleType role);
}
