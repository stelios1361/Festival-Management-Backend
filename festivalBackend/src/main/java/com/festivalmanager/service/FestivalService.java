package com.festivalmanager.service;

import com.festivalmanager.model.VendorManagement;
import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.festival.*;
import com.festivalmanager.enums.FestivalRoleType;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.*;
import com.festivalmanager.repository.*;
import com.festivalmanager.security.UserSecurityService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling all festival-related operations: creation,
 * management of festival roles, and other festival operations.
 */
@Service
public class FestivalService {

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private FestivalUserRoleRepository festivalUserRoleRepository;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private UserRepository userRepository;

    // -------------------- CREATE FESTIVAL --------------------
    /**
     * Creates a new festival and assigns the requester as an ORGANIZER.
     * <p>
     * This method validates the requester via {@link UserSecurityService},
     * checks for festival name uniqueness, validates required fields (dates and
     * venue), and creates both the Festival entity and the corresponding
     * organizer role.
     *
     * @param request the festival creation request containing name, dates,
     * venue, and description
     * @return ApiResponse containing festival ID, name, and organizer username
     * @throws ApiException if requester is invalid, festival name exists, or
     * required fields are missing
     */
    @Transactional
    public ApiResponse<Map<String, Object>> createFestival(FestivalCreateRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        // Check festival name uniqueness
        if (festivalRepository.existsByName(request.getName())) {
            throw new ApiException("Festival with this name already exists", HttpStatus.CONFLICT);

        }

        // Validate required fields
        if (request.getDates() == null || request.getDates().isEmpty()) {
            throw new ApiException("At least one date must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getVenue() == null || request.getVenue().isBlank()) {
            throw new ApiException("Venue must be provided", HttpStatus.BAD_REQUEST);
        }

        // Create new festival
        Festival festival = new Festival();
        festival.setName(request.getName());
        festival.setDescription(request.getDescription());
        festival.setVenue(request.getVenue());
        festival.setState(Festival.FestivalState.CREATED);
        festival.setDates(request.getDates());

        Festival savedFestival = festivalRepository.save(festival);

        // Assign requester as ORGANIZER for this festival
        FestivalUserRole role = new FestivalUserRole();
        role.setFestival(savedFestival);
        role.setUser(requester);
        role.setRole(FestivalRoleType.ORGANIZER);
        festivalUserRoleRepository.save(role);

        // Build response data
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedFestival.getId());
        data.put("name", savedFestival.getName());
        data.put("organizer", requester.getUsername());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Festival created successfully",
                data
        );
    }

    // -------------------- UPDATE FESTIVAL --------------------
    /**
     * Updates a festival's details. Only allows certain updates before the
     * festival reaches the ANNOUNCED state.
     * <p>
     * The festival creator cannot be removed from organizers. Venue layout,
     * budget, and vendor management can only be updated before the festival is
     * announced.
     *
     * @param request the festival update request containing new details
     * @return ApiResponse with updated festival information
     * @throws ApiException if festival or requester not found, unauthorized, or
     * invalid data
     */
    @Transactional
    public ApiResponse<Map<String, Object>> updateFestival(FestivalUpdateRequest request) {
        // Validate requester
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check requester is an organizer for the festival
        isOrganizerForFestival(requester, festival);

        boolean isAnnounced = festival.getState() == Festival.FestivalState.ANNOUNCED;

        // Update basic info
        if (request.getName() != null && !request.getName().isBlank()) {
            if (!festival.getName().equals(request.getName()) && festivalRepository.existsByName(request.getName())) {
                throw new ApiException("Festival with this name already exists", HttpStatus.CONFLICT);
            }
            festival.setName(request.getName());
        }
        if (request.getDescription() != null) {
            festival.setDescription(request.getDescription());
        }
        if (request.getDates() != null && !request.getDates().isEmpty()) {
            festival.setDates(new HashSet<>(request.getDates()));
        }

        // Update nested objects only if festival not ANNOUNCED
        if (!isAnnounced) {
            updateVenueLayout(festival, request.getVenueLayout());
            updateBudget(festival, request.getBudget());
            updateVendorManagement(festival, request.getVendorManagement());
        }

        // Update organizers and staff
        updateFestivalRoles(festival, request.getOrganizers(), request.getStaff());

        Festival updatedFestival = festivalRepository.save(festival);

        // Build response
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

    //-------------------- ADD ORGANIZERS --------------------
    /**
     * Adds one or more users as ORGANIZERS for a festival.
     * <p>
     * Only a current organizer of the festival can add new organizers. Users
     * already assigned as organizers are ignored.
     *
     * @param request the request containing the festival ID, requester
     * username, token, and usernames to add
     * @return ApiResponse containing the updated list of organizers
     * @throws ApiException if requester is invalid, not an organizer, festival
     * not found, or a username does not exist
     */
    @Transactional
    public ApiResponse<Map<String, Object>> addOrganizers(AddOrganizersRequest request) {
        // Validate requester
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check requester is an organizer for the festival
        isOrganizerForFestival(requester, festival);

        // Validate usernames list
        if (request.getUsernames() == null || request.getUsernames().isEmpty()) {
            throw new ApiException("Usernames list cannot be null or empty", HttpStatus.BAD_REQUEST);
        }

        Set<FestivalUserRole> currentRoles = festival.getUserRoles();
        Map<String, FestivalUserRole> rolesMap = currentRoles.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUsername(), r -> r));

        // Add new organizers
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

        // Build response
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

    //-------------------- ADD STAFF --------------------
    /**
     * Adds one or more staff members to a festival. Only organizers of the
     * festival can perform this action. Existing staff members are ignored.
     *
     * @param request the request containing festival ID, token, requester
     * username, and staff usernames
     * @return ApiResponse containing updated list of staff members
     * @throws ApiException if token is invalid, requester not found, or
     * requester is not an organizer
     */
    @Transactional
    public ApiResponse<Map<String, Object>> addStaff(AddStaffRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        Set<FestivalUserRole> currentRoles = festival.getUserRoles();
        Map<String, FestivalUserRole> rolesMap = currentRoles.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUsername(), r -> r));

        // Add new staff members
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
            // Already staff -> ignored
        }

        Festival updatedFestival = festivalRepository.save(festival);

        // Build response
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

    //-------------------- SEARCH FESTIVAL --------------------
    /**
     * Searches for festivals based on given criteria. If no criteria are
     * provided, all festivals are returned. Results are sorted by the earliest
     * date, then by name.
     *
     * @param request FestivalSearchRequest containing search criteria
     * @return ApiResponse with a list of matching festivals
     */
    @Transactional
    public ApiResponse<Map<String, Object>> searchFestivals(FestivalSearchRequest request) {

        List<Festival> festivals = festivalRepository.findAll();
        User requester = null;

        if (request.getRequesterUsername() != null && !request.getRequesterUsername().isEmpty()) {
            // Validate requester if username + token provided
            requester = userSecurityService.validateRequester(
                    request.getRequesterUsername(),
                    request.getToken()
            );
        }

        // Apply filters
        if (request.getName() != null && !request.getName().isBlank()) {
            String[] words = request.getName().trim().split("\\s+");
            festivals = festivals.stream()
                    .filter(f -> Arrays.stream(words)
                    .allMatch(word -> f.getName().toLowerCase().contains(word.toLowerCase())))
                    .toList();
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            String[] words = request.getDescription().trim().split("\\s+");
            festivals = festivals.stream()
                    .filter(f -> Arrays.stream(words)
                    .allMatch(word -> f.getDescription().toLowerCase().contains(word.toLowerCase())))
                    .toList();
        }

        if (request.getVenue() != null && !request.getVenue().isBlank()) {
            String[] words = request.getVenue().trim().split("\\s+");
            festivals = festivals.stream()
                    .filter(f -> Arrays.stream(words)
                    .allMatch(word -> f.getVenue().toLowerCase().contains(word.toLowerCase())))
                    .toList();
        }

        if (request.getStartDate() != null || request.getEndDate() != null) {
            festivals = festivals.stream()
                    .filter(f -> f.getDates().stream().anyMatch(date
                    -> (request.getStartDate() == null || !date.isBefore(request.getStartDate()))
                    && (request.getEndDate() == null || !date.isAfter(request.getEndDate()))
            ))
                    .toList();
        }

        // Sort by earliest date then name
        festivals = festivals.stream()
                .sorted(Comparator
                        .comparing((Festival f) -> f.getDates().stream().min(LocalDate::compareTo).orElse(LocalDate.MAX))
                        .thenComparing(Festival::getName))
                .toList();

        User finalRequester = requester;

        List<FestivalSearchResponseDTO> results = festivals.stream()
                .map(f -> mapFestival(f, finalRequester))
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("festivals", results);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Festivals retrieved successfully",
                data
        );
    }
    
    //-------------------- VIEW FESTIVAL --------------------
    /**
     * Searches for festival based on given festival id.
     * Results are sorted by the earliest
     * date, then by name.
     *
     * @param request FestivalViewRequest 
     * @return ApiResponse with the matching festival
     */
    @Transactional
    public ApiResponse<Map<String, Object>> viewFestival(FestivalViewRequest request) {

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        User requester = null;

        if (request.getRequesterUsername() != null && !request.getRequesterUsername().isEmpty()) {
            // Validate requester if username + token provided
            requester = userSecurityService.validateRequester(
                    request.getRequesterUsername(),
                    request.getToken()
            );
        }

        // Map to view based on role
        FestivalSearchResponseDTO dto = mapFestival(festival, requester);

        Map<String, Object> data = new HashMap<>();
        data.put("festival", dto);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Festival retrieved successfully",
                data
        );
    }

    //-------------------- DELETE FESTIVAL --------------------
    /**
     * Deletes a festival. Only organizers of the festival can delete it, and
     * only if the festival is in its initial CREATED state.
     *
     * @param request the festival delete request containing festival ID, token,
     * and requester username
     * @return ApiResponse containing the deleted festival's ID and name
     * @throws ApiException if token is invalid, requester not found, requester
     * is not an organizer, or festival is not in CREATED state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> deleteFestival(FestivalDeleteRequest request) {
        // Validate requester
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check festival state
        if (festival.getState() != Festival.FestivalState.CREATED) {
            throw new ApiException("Only festivals in CREATED state can be deleted", HttpStatus.FORBIDDEN);
        }

        // Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        // Delete festival
        festivalRepository.delete(festival);

        // Build response
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

    //-------------------- SUBMISION START --------------------
    /**
     * Starts the submission phase for a festival. Only organizers of the
     * festival can start submission. The festival must be in CREATED state;
     * after this operation, its state will transition to SUBMISSION.
     *
     * @param request the submission start request containing festival ID,
     * token, and requester username
     * @return ApiResponse containing festival ID, name, and new state
     * @throws ApiException if token is invalid, requester not found, requester
     * is not an organizer, or festival is not in CREATED state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> startSubmission(SubmissionStartRequest request) {
        // Validate requester
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check festival state
        if (festival.getState() != Festival.FestivalState.CREATED) {
            throw new ApiException("Festival is not in CREATED state", HttpStatus.FORBIDDEN);
        }

        // Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        // Update festival state to SUBMISSION
        festival.setState(Festival.FestivalState.SUBMISSION);
        festivalRepository.save(festival);

        // Build response
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

    //-------------------- STAGE MANAGER ASSIGNMENT START --------------------
    /**
     * Starts the stage manager assignment phase for a festival. Only organizers
     * of the festival can perform this action. The festival must be in
     * SUBMISSION state; after this operation, its state will transition to
     * ASSIGNMENT.
     *
     * @param request the request containing festival ID, token, and requester
     * username
     * @return ApiResponse containing festival ID, name, and new state
     * @throws ApiException if token is invalid, requester not found, requester
     * is not an organizer, or festival is not in SUBMISSION state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> startStageManagerAssignment(StageManagerAssignmentStartRequest request) {
        // Validate requester
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check festival state
        if (festival.getState() != Festival.FestivalState.SUBMISSION) {
            throw new ApiException("Festival is not in SUBMISSION state", HttpStatus.FORBIDDEN);
        }

        // Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        // Update festival state to ASSIGNMENT
        festival.setState(Festival.FestivalState.ASSIGNMENT);
        festivalRepository.save(festival);

        // Build response
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

    //-------------------- REVIEW START --------------------
    /**
     * Starts the review phase for a festival.
     * <p>
     * Only organizers of the festival can perform this action. The festival
     * must currently be in the ASSIGNMENT state. After executing this action,
     * the festival state becomes REVIEW, allowing the review of submitted
     * performances.
     *
     * @param request the request containing festival ID and requester info
     * @return ApiResponse with updated festival information
     * @throws ApiException if token is invalid, requester is not an organizer,
     * festival not found, or festival is in the wrong state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> startReview(ReviewStartRequest request) {
        // Validate requester
        User requester = userSecurityService.validateRequester(request.getRequesterUsername(), request.getToken());

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check festival state
        if (festival.getState() != Festival.FestivalState.ASSIGNMENT) {
            throw new ApiException("Festival is not in ASSIGNMENT state", HttpStatus.FORBIDDEN);
        }

        // Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        // Check festival state
        festival.setState(Festival.FestivalState.REVIEW);
        festivalRepository.save(festival);

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

    //-------------------- SCHEDULE MAKING --------------------
    /**
     * Starts the schedule making phase for a festival.
     * <p>
     * Only organizers of the festival can perform this action. The festival
     * must currently be in the REVIEW state. After executing this action, the
     * festival state becomes SCHEDULING, allowing approval/rejection of
     * performances and tentative scheduling.
     *
     * @param request the request containing festival ID and requester info
     * @return ApiResponse with updated festival information
     * @throws ApiException if token is invalid, requester is not an organizer,
     * festival not found, or festival is in the wrong state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> startScheduleMaking(ScheduleMakingRequest request) {

        // Validate requester
        User requester = userSecurityService.validateRequester(request.getRequesterUsername(), request.getToken());

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check festival state
        if (festival.getState() != Festival.FestivalState.REVIEW) {
            throw new ApiException("Festival is not in REVIEW state", HttpStatus.FORBIDDEN);
        }

        // Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        // Check festival state
        festival.setState(Festival.FestivalState.SCHEDULING);
        festivalRepository.save(festival);

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

    //-------------------- FINAL SUBMITION START --------------------
    /**
     * Starts the final submission phase for a festival.
     * <p>
     * Only organizers of the festival can perform this action. The festival
     * must currently be in the SCHEDULING state. After executing this action,
     * the festival state becomes FINAL_SUBMISSION, allowing artists to submit
     * final versions of approved performances.
     *
     * @param request the request containing festival ID and requester info
     * @return ApiResponse with updated festival information
     * @throws ApiException if token is invalid, requester is not an organizer,
     * festival not found, or festival is in the wrong state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> startFinalSubmission(FinalSubmissionStartRequest request) {
        // Validate requester
        User requester = userSecurityService.validateRequester(request.getRequesterUsername(), request.getToken());

        // Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // Check festival state
        if (festival.getState() != Festival.FestivalState.SCHEDULING) {
            throw new ApiException("Festival is not in SCHEDULING state", HttpStatus.FORBIDDEN);
        }

        // Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        // Set festival state
        festival.setState(Festival.FestivalState.FINAL_SUBMISSION);
        festivalRepository.save(festival);

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

    //-------------------- DECISION MAKING --------------------
    /**
     * Starts the decision-making phase for a festival.
     * <p>
     * This action is permitted only if the festival is in FINAL_SUBMISSION
     * state. All performances that were not submitted by this point are
     * automatically marked as REJECTED. Only organizers of the festival can
     * perform this action.
     *
     * @param request the decision-making request containing festival ID and
     * requester info
     * @return ApiResponse with updated festival state and relevant info
     * @throws ApiException if festival not found, requester invalid,
     * unauthorized, or wrong state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> startDecisionMaking(DecisionMakingRequest request) {
        //Validate requester
        User requester = userSecurityService.validateRequester(request.getRequesterUsername(), request.getToken());

        //Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        //Check festival state
        if (festival.getState() != Festival.FestivalState.FINAL_SUBMISSION) {
            throw new ApiException("Festival is not in FINAL_SUBMISSION state", HttpStatus.FORBIDDEN);
        }

        //Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        //Reject all performances that are not submitted
        festival.getPerformances().forEach(performance -> {
            if (performance.getState() != Performance.PerformanceState.SUBMITTED) {
                performance.setState(Performance.PerformanceState.REJECTED);
                performance.setReviewerComments("AUTOMATICALY REJECTED - NOT SUBMITTED");
            }
        });

        //Update festival state to DECISION
        festival.setState(Festival.FestivalState.DECISION);
        festivalRepository.save(festival);

        //Build response
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

    //-------------------- FESTIVAL ANNOUNCEMENT --------------------
    /**
     * Announces the festival publicly.
     * <p>
     * This action is permitted only if the festival is in DECISION state. After
     * executing this action, the festival state becomes ANNOUNCED. Only
     * organizers of the festival can perform this action.
     *
     * @param request the festival announcement request containing festival ID
     * and requester info
     * @return ApiResponse with updated festival state and relevant info
     * @throws ApiException if festival not found, requester invalid,
     * unauthorized, or wrong state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> announceFestival(FestivalAnnouncementRequest request) {
        //Validate requester
        User requester = userSecurityService.validateRequester(request.getRequesterUsername(), request.getToken());

        //Find festival
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        //Check festival state
        if (festival.getState() != Festival.FestivalState.DECISION) {
            throw new ApiException("Festival is not in DECISION state", HttpStatus.FORBIDDEN);
        }

        //Check requester is an organizer
        isOrganizerForFestival(requester, festival);

        //Update festival state to ANNOUNCED
        festival.setState(Festival.FestivalState.ANNOUNCED);
        festivalRepository.save(festival);

        //Build response
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

    // -------------------- HELPERS --------------------
    private void updateVenueLayout(Festival festival, VenueLayoutDTO dto) {
        if (dto == null) {
            throw new ApiException("Venue layout data cannot be null", HttpStatus.BAD_REQUEST);
        }
        VenueLayout layout = festival.getVenueLayout();
        if (layout == null) {
            layout = new VenueLayout();
            layout.setFestival(festival);
        }

        if (dto.getStages() == null || dto.getStages().isEmpty()) {
            throw new ApiException("Stages list cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        if (dto.getVendorAreas() == null || dto.getVendorAreas().isEmpty()) {
            throw new ApiException("Vendor areas cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        if (dto.getFacilities() == null || dto.getFacilities().isEmpty()) {
            throw new ApiException("Facilities cannot be null or empty", HttpStatus.BAD_REQUEST);
        }

        layout.setStages(dto.getStages());
        layout.setVendorAreas(dto.getVendorAreas());
        layout.setFacilities(dto.getFacilities());
        festival.setVenueLayout(layout);
    }

    private void updateBudget(Festival festival, BudgetDTO dto) {
        if (dto == null) {
            throw new ApiException("Budget data cannot be null", HttpStatus.BAD_REQUEST);
        }
        Budget budget = festival.getBudget();
        if (budget == null) {
            budget = new Budget();
            budget.setFestival(festival);
        }

        if (dto.getTracking() == null) {
            throw new ApiException("Budget tracking info cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (dto.getCosts() == null) {
            throw new ApiException("Budget costs cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (dto.getLogistics() == null) {
            throw new ApiException("Budget logistics info cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (dto.getExpectedRevenue() == null) {
            throw new ApiException("Expected revenue cannot be null", HttpStatus.BAD_REQUEST);
        }

        budget.setTracking(dto.getTracking());
        budget.setCosts(dto.getCosts());
        budget.setLogistics(dto.getLogistics());
        budget.setExpectedRevenue(dto.getExpectedRevenue());
        festival.setBudget(budget);
    }

    private void updateVendorManagement(Festival festival, VendorManagementDTO dto) {
        if (dto == null) {
            throw new ApiException("Vendor management data cannot be null", HttpStatus.BAD_REQUEST);
        }
        VendorManagement vm = festival.getVendorManagement();
        if (vm == null) {
            vm = new VendorManagement();
            vm.setFestival(festival);
        }

        if (dto.getFoodStalls() == null) {
            throw new ApiException("Food stalls cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (dto.getMerchandiseBooths() == null) {
            throw new ApiException("Merchandise booths cannot be null", HttpStatus.BAD_REQUEST);
        }

        vm.setFoodStalls(dto.getFoodStalls());
        vm.setMerchandiseBooths(dto.getMerchandiseBooths());
        festival.setVendorManagement(vm);
    }

    private void updateFestivalRoles(Festival festival, Set<String> organizers, Set<String> staff) {
        Set<FestivalUserRole> currentRoles = festival.getUserRoles();
        Map<String, FestivalUserRole> rolesMap = currentRoles.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUsername(), r -> r));

        // Organizers
        if (organizers == null) {
            throw new ApiException("Organizers set cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (organizers.isEmpty()) {
            throw new ApiException("At least one organizer must be provided", HttpStatus.BAD_REQUEST);
        }

        for (String username : organizers) {
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

        // Prevent removing creator (earliest organizer)
        User creator = currentRoles.stream()
                .filter(r -> r.getRole() == FestivalRoleType.ORGANIZER)
                .min(Comparator.comparing(FestivalUserRole::getId))
                .get().getUser();

        currentRoles.removeIf(r -> r.getRole() == FestivalRoleType.ORGANIZER
                && !organizers.contains(r.getUser().getUsername())
                && !r.getUser().equals(creator));

        // Staff
        if (staff == null) {
            throw new ApiException("Staff set cannot be null", HttpStatus.BAD_REQUEST);
        }
        // Empty staff is allowed; means no staff assigned
        currentRoles.removeIf(r -> r.getRole() == FestivalRoleType.STAFF);

        for (String username : staff) {
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

    private boolean isOrganizerForFestival(User user, Festival festival) {
        if (!festivalUserRoleRepository.existsByFestivalAndUserAndRole(festival, user, FestivalRoleType.ORGANIZER)) {
            throw new ApiException("Only organizers of this festival can add new organizers", HttpStatus.FORBIDDEN);
        }
        return true;
    }

    private FestivalSearchResponseDTO mapFestival(Festival festival, User requester) {
        FestivalSearchResponseDTO dto = new FestivalSearchResponseDTO();
        dto.setId(festival.getId());
        dto.setName(festival.getName());
        dto.setDescription(festival.getDescription());
        dto.setVenue(festival.getVenue());
        dto.setDates(festival.getDates());

        boolean isOrganizer = requester != null && festival.getUserRoles().stream()
                .anyMatch(r -> r.getUser().equals(requester) && r.getRole() == FestivalRoleType.ORGANIZER);

        if (isOrganizer) {
            dto.setOrganizers(festival.getUserRoles().stream()
                    .filter(r -> r.getRole() == FestivalRoleType.ORGANIZER)
                    .map(r -> r.getUser().getUsername())
                    .toList());

            dto.setStaff(festival.getUserRoles().stream()
                    .filter(r -> r.getRole() == FestivalRoleType.STAFF)
                    .map(r -> r.getUser().getUsername())
                    .toList());

            // Nested entities
            VenueLayout layout = festival.getVenueLayout();
            if (layout != null) {
                VenueLayoutDTO layoutDTO = new VenueLayoutDTO();
                layoutDTO.setStages(layout.getStages());
                layoutDTO.setVendorAreas(layout.getVendorAreas());
                layoutDTO.setFacilities(layout.getFacilities());
                dto.setVenueLayout(layoutDTO);
            }

            Budget budget = festival.getBudget();
            if (budget != null) {
                BudgetDTO budgetDTO = new BudgetDTO();
                budgetDTO.setTracking(budget.getTracking());
                budgetDTO.setCosts(budget.getCosts());
                budgetDTO.setLogistics(budget.getLogistics());
                budgetDTO.setExpectedRevenue(budget.getExpectedRevenue());
                dto.setBudget(budgetDTO);
            }

            VendorManagement vm = festival.getVendorManagement();
            if (vm != null) {
                VendorManagementDTO vmDTO = new VendorManagementDTO();
                vmDTO.setFoodStalls(vm.getFoodStalls());
                vmDTO.setMerchandiseBooths(vm.getMerchandiseBooths());
                dto.setVendorManagement(vmDTO);
            }
        }

        return dto;
    }

}
