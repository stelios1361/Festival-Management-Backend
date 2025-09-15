package com.festivalmanager.service;

import com.festivalmanager.dto.festival.DecisionMakingRequest;
import com.festivalmanager.dto.festival.ReviewStartRequest;
import com.festivalmanager.dto.festival.ScheduleMakingRequest;
import com.festivalmanager.dto.festival.StageManagerAssignmentStartRequest;
import com.festivalmanager.dto.festival.SubmissionStartRequest;
import com.festivalmanager.dto.festival.FestivalUpdateRequest;
import com.festivalmanager.dto.festival.FestivalCreateRequest;
import com.festivalmanager.dto.festival.FestivalAnnouncementRequest;
import com.festivalmanager.dto.festival.FinalSubmissionStartRequest;
import com.festivalmanager.dto.festival.FestivalDeleteRequest;
import com.festivalmanager.dto.festival.AddStaffRequest;
import com.festivalmanager.dto.festival.AddOrganizersRequest;
import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.enums.FestivalRoleType;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.*;
import com.festivalmanager.repository.*;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FestivalService {

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private FestivalUserRoleRepository festivalUserRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    /**
     * Creates a new festival and sets the requester as ORGANIZER.
     */
    @Transactional
    public ApiResponse<Map<String, Object>> createFestival(FestivalCreateRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Check festival name uniqueness
        if (festivalRepository.existsByName(request.getName())) {
            throw new ApiException("Festival with this name already exists", HttpStatus.CONFLICT);
        }

        // 4. Validate required fields
        if (request.getDates() == null || request.getDates().isEmpty()) {
            throw new ApiException("At least one date must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getVenue() == null || request.getVenue().isBlank()) {
            throw new ApiException("Venue must be provided", HttpStatus.BAD_REQUEST);
        }

        // 5. Create new festival
        Festival festival = new Festival();
        festival.setName(request.getName());
        festival.setDescription(request.getDescription());
        festival.setVenue(request.getVenue());
        festival.setState(Festival.FestivalState.CREATED);

        // Convert LocalDate -> LocalDateTime
        Set<LocalDateTime> dateTimes = new HashSet<>();
        request.getDates().forEach(date -> dateTimes.add(date.atStartOfDay()));
        festival.setDates(dateTimes);

        Festival savedFestival = festivalRepository.save(festival);

        // 6. Assign requester as ORGANIZER for this festival
        FestivalUserRole role = new FestivalUserRole();
        role.setFestival(savedFestival);
        role.setUser(requester);
        role.setRole(FestivalRoleType.ORGANIZER);
        festivalUserRoleRepository.save(role);

        // 7. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedFestival.getId());
        data.put("identifier", savedFestival.getIdentifier());
        data.put("name", savedFestival.getName());
        data.put("state", savedFestival.getState().name());
        data.put("organizers", List.of(requester.getUsername())); // since only one initially

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Festival created successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> updateFestival(FestivalUpdateRequest request) {
        //Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        //Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        //Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        boolean isAnnounced = festival.getState() == Festival.FestivalState.ANNOUNCED;

        //Update basic info
        if (request.getName() != null && !request.getName().isBlank()) {
            if (!festival.getName().equals(request.getName())
                    && festivalRepository.existsByName(request.getName())) {
                throw new ApiException("Festival with this name already exists", HttpStatus.CONFLICT);
            }
            festival.setName(request.getName());
        }

        if (request.getDescription() != null) {
            festival.setDescription(request.getDescription());
        }

        if (request.getDates() != null && !request.getDates().isEmpty()) {
            Set<LocalDateTime> dateTimes = new HashSet<>();
            request.getDates().forEach(d -> dateTimes.add(d.atStartOfDay()));
            festival.setDates(dateTimes);
        }

        //Update venue, budget, vendor only if festival is not ANNOUNCED
        if (!isAnnounced) {
            if (request.getStages() != null) {
                festival.setStages(request.getStages());
            }
            if (request.getVendorAreas() != null) {
                festival.setVendorAreas(request.getVendorAreas());
            }

            if (request.getTracking() != null || request.getCosts() != null
                    || request.getLogistics() != null || request.getExpectedRevenue() != null) {

                Festival.Budget budget = festival.getBudget() != null ? festival.getBudget() : new Festival.Budget();
                if (request.getTracking() != null) {
                    budget.setTracking(request.getTracking());
                }
                if (request.getCosts() != null) {
                    budget.setCosts(request.getCosts());
                }
                if (request.getLogistics() != null) {
                    budget.setLogistics(request.getLogistics());
                }
                if (request.getExpectedRevenue() != null) {
                    budget.setExpectedRevenue(request.getExpectedRevenue());
                }
                festival.setBudget(budget);
            }

            if (request.getFoodStalls() != null || request.getMerchandiseBooths() != null) {
                Festival.VendorManagement vm = festival.getVendorManagement() != null ? festival.getVendorManagement() : new Festival.VendorManagement();
                if (request.getFoodStalls() != null) {
                    vm.setFoodStalls(request.getFoodStalls());
                }
                if (request.getMerchandiseBooths() != null) {
                    vm.setMerchandiseBooths(request.getMerchandiseBooths());
                }
                festival.setVendorManagement(vm);
            }
        }

        //Update organizers & staff
        Set<FestivalUserRole> currentRoles = festival.getUserRoles();
        Map<String, FestivalUserRole> rolesMap = currentRoles.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUsername(), r -> r));

        // Organizers
        if (request.getOrganizers() != null) {
            // Add new organizers
            for (String username : request.getOrganizers()) {
                if (!rolesMap.containsKey(username)) {
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new ApiException("User not found: " + username, HttpStatus.NOT_FOUND));
                    FestivalUserRole role = new FestivalUserRole();
                    role.setFestival(festival);
                    role.setUser(user);
                    role.setRole(FestivalRoleType.ORGANIZER);
                    festivalUserRoleRepository.save(role);
                    currentRoles.add(role);
                }
            }

            // Prevent removing creator
            User creator = currentRoles.stream()
                    .filter(r -> r.getRole() == FestivalRoleType.ORGANIZER)
                    .min(Comparator.comparing(FestivalUserRole::getId))
                    .get().getUser();

            currentRoles.removeIf(r -> r.getRole() == FestivalRoleType.ORGANIZER
                    && !request.getOrganizers().contains(r.getUser().getUsername())
                    && !r.getUser().equals(creator));
        }

        // Staff
        if (request.getStaff() != null) {
            currentRoles.removeIf(r -> r.getRole() == FestivalRoleType.STAFF);
            for (String username : request.getStaff()) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new ApiException("User not found: " + username, HttpStatus.NOT_FOUND));
                FestivalUserRole role = new FestivalUserRole();
                role.setFestival(festival);
                role.setUser(user);
                role.setRole(FestivalRoleType.STAFF);
                festivalUserRoleRepository.save(role);
                currentRoles.add(role);
            }
        }

        //Save festival
        Festival updatedFestival = festivalRepository.save(festival);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", updatedFestival.getId());
        data.put("identifier", updatedFestival.getIdentifier());
        data.put("name", updatedFestival.getName());
        data.put("state", updatedFestival.getState().name());
        data.put("organizers", updatedFestival.getUserRoles().stream()
                .filter(r -> r.getRole() == FestivalRoleType.ORGANIZER)
                .map(r -> r.getUser().getUsername())
                .toList());
        data.put("staff", updatedFestival.getUserRoles().stream()
                .filter(r -> r.getRole() == FestivalRoleType.STAFF)
                .map(r -> r.getUser().getUsername())
                .toList());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Festival updated successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> addOrganizers(AddOrganizersRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        Set<FestivalUserRole> currentRoles = festival.getUserRoles();
        Map<String, FestivalUserRole> rolesMap = currentRoles.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUsername(), r -> r));

        // 4. Add new organizers
        for (String username : request.getUsernames()) {
            if (!rolesMap.containsKey(username)) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new ApiException("User not found: " + username, HttpStatus.NOT_FOUND));

                FestivalUserRole role = new FestivalUserRole();
                role.setFestival(festival);
                role.setUser(user);
                role.setRole(FestivalRoleType.ORGANIZER);

                festivalUserRoleRepository.save(role);
                currentRoles.add(role);
            }
            // If user is already an organizer, ignore
        }

        Festival updatedFestival = festivalRepository.save(festival);

        // 5. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", updatedFestival.getId());
        data.put("identifier", updatedFestival.getIdentifier());
        data.put("organizers", updatedFestival.getUserRoles().stream()
                .filter(r -> r.getRole() == FestivalRoleType.ORGANIZER)
                .map(r -> r.getUser().getUsername())
                .toList());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Organizers added successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> addStaff(AddStaffRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        Set<FestivalUserRole> currentRoles = festival.getUserRoles();
        Map<String, FestivalUserRole> rolesMap = currentRoles.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUsername(), r -> r));

        // 4. Add new staff members
        for (String username : request.getUsernames()) {
            if (!rolesMap.containsKey(username)) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new ApiException("User not found: " + username, HttpStatus.NOT_FOUND));

                FestivalUserRole role = new FestivalUserRole();
                role.setFestival(festival);
                role.setUser(user);
                role.setRole(FestivalRoleType.STAFF);

                festivalUserRoleRepository.save(role);
                currentRoles.add(role);
            }
            // If user is already staff, ignore
        }

        Festival updatedFestival = festivalRepository.save(festival);

        // 5. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", updatedFestival.getId());
        data.put("identifier", updatedFestival.getIdentifier());
        data.put("staff", updatedFestival.getUserRoles().stream()
                .filter(r -> r.getRole() == FestivalRoleType.STAFF)
                .map(r -> r.getUser().getUsername())
                .toList());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Staff members added successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> deleteFestival(FestivalDeleteRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in CREATED state
        if (festival.getState() != Festival.FestivalState.CREATED) {
            throw new ApiException("Only festivals in CREATED state can be deleted", HttpStatus.FORBIDDEN);
        }

        // 5. Check if requester is an organizer
        boolean isOrganizer = festival.getUserRoles().stream()
                .anyMatch(r -> r.getRole() == FestivalRoleType.ORGANIZER
                && r.getUser().equals(requester));

        if (!isOrganizer) {
            throw new ApiException("Only organizers can delete this festival", HttpStatus.FORBIDDEN);
        }

        // 6. Delete festival
        festivalRepository.delete(festival);

        // 7. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Festival deleted successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> startSubmission(SubmissionStartRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in CREATED state
        if (festival.getState() != Festival.FestivalState.CREATED) {
            throw new ApiException("Festival is not in CREATED state", HttpStatus.FORBIDDEN);
        }

        // 5. Update festival state to SUBMISSION
        festival.setState(Festival.FestivalState.SUBMISSION);
        festivalRepository.save(festival);

        // 6. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());
        data.put("state", festival.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Submission started successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> startStageManagerAssignment(StageManagerAssignmentStartRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in SUBMISSION state
        if (festival.getState() != Festival.FestivalState.SUBMISSION) {
            throw new ApiException("Festival is not in SUBMISSION state", HttpStatus.FORBIDDEN);
        }

        // 5. Update festival state to ASSIGNMENT
        festival.setState(Festival.FestivalState.ASSIGNMENT);
        festivalRepository.save(festival);

        // 6. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());
        data.put("state", festival.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Stage manager assignment started successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> startReview(ReviewStartRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in ASSIGNMENT state
        if (festival.getState() != Festival.FestivalState.ASSIGNMENT) {
            throw new ApiException("Festival is not in ASSIGNMENT state", HttpStatus.FORBIDDEN);
        }

        // 5. Update festival state to REVIEW
        festival.setState(Festival.FestivalState.REVIEW);
        festivalRepository.save(festival);

        // 6. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());
        data.put("state", festival.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Review started successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> startScheduleMaking(ScheduleMakingRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in REVIEW state
        if (festival.getState() != Festival.FestivalState.REVIEW) {
            throw new ApiException("Festival is not in REVIEW state", HttpStatus.FORBIDDEN);
        }

        // 5. Update festival state to SCHEDULING
        festival.setState(Festival.FestivalState.SCHEDULING);
        festivalRepository.save(festival);

        // 6. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());
        data.put("state", festival.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Schedule making started successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> startFinalSubmission(FinalSubmissionStartRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in SCHEDULING state
        if (festival.getState() != Festival.FestivalState.SCHEDULING) {
            throw new ApiException("Festival is not in SCHEDULING state", HttpStatus.FORBIDDEN);
        }

        // 5. Update festival state to FINAL_SUBMISSION
        festival.setState(Festival.FestivalState.FINAL_SUBMISSION);
        festivalRepository.save(festival);

        // 6. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());
        data.put("state", festival.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Final submission started successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> makeDecision(DecisionMakingRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in FINAL_SUBMISSION state
        if (festival.getState() != Festival.FestivalState.FINAL_SUBMISSION) {
            throw new ApiException("Festival is not in FINAL_SUBMISSION state", HttpStatus.FORBIDDEN);
        }

        // 5. Process performances
        festival.getPerformances().forEach(performance -> {
            // TODO: If performance was approved but not finally submitted,
            // automatically mark it as REJECTED
            // e.g., if (performance.getStatus() == PerformanceStatus.APPROVED && !performance.isFinallySubmitted()) {
            //          performance.setStatus(PerformanceStatus.REJECTED);
            //      }
        });

        // 6. Update festival state to DECISION
        festival.setState(Festival.FestivalState.DECISION);
        festivalRepository.save(festival);

        // 7. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());
        data.put("state", festival.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Decision making started successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> announceFestival(FestivalAnnouncementRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requesting user
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // 3. Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // 4. Check if festival is in DECISION state
        if (festival.getState() != Festival.FestivalState.DECISION) {
            throw new ApiException("Festival is not in DECISION state", HttpStatus.FORBIDDEN);
        }

        // 5. Update festival state to ANNOUNCED
        festival.setState(Festival.FestivalState.ANNOUNCED);
        festivalRepository.save(festival);

        // 6. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", festival.getId());
        data.put("name", festival.getName());
        data.put("state", festival.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Festival announced successfully",
                data
        );
    }

}
