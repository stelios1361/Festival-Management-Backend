package com.festivalmanager.service;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.performance.*;
import com.festivalmanager.enums.FestivalRoleType;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.*;
import com.festivalmanager.model.Performance.PerformanceState;
import com.festivalmanager.repository.*;
import com.festivalmanager.security.UserSecurityService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service class for managing festival performances.
 * <p>
 * Provides methods for creating, updating, submitting, withdrawing, assigning
 * staff, reviewing, approving, rejecting, final submitting, accepting, viewing,
 * and searching performances.
 * <p>
 * Handles role-based access control: - ARTIST: Can create, update, submit,
 * withdraw, finalize performances. - STAFF: Can review assigned performances. -
 * ORGANIZER: Can assign staff, approve/reject, accept performances.
 * <p>
 * All methods return an ApiResponse containing status, message, and relevant
 * data.
 */
@Service
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FestivalUserRoleRepository festivalUserRoleRepository;

    @Autowired
    private UserSecurityService userSecurityService;

    /**
     * Creates a new performance for a given festival.
     *
     * @param request PerformanceCreateRequest containing performance details
     * and band members
     * @return ApiResponse with performance ID, identifier, name, state, and
     * main artist
     * @throws ApiException if validation fails or festival/performance already
     * exists
     */
    @Transactional
    public ApiResponse<Map<String, Object>> createPerformance(PerformanceCreateRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find festival 
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        //Check performance name uniqueness within festival
        boolean nameExists = festival.getPerformances().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));
        if (nameExists) {
            throw new ApiException("Performance with this name already exists in this festival", HttpStatus.CONFLICT);
        }

        //Validate required fields 
        if (request.getGenre() == null || request.getGenre().isBlank()) {
            throw new ApiException("Genre must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getDuration() == null || request.getDuration() <= 0) {
            throw new ApiException("Valid duration must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ApiException("Name must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ApiException("Description must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getBandMemberIds() == null || request.getBandMemberIds().isEmpty()) {
            throw new ApiException("At least one band member must be provided", HttpStatus.BAD_REQUEST);
        }

        //Create performance entity 
        Performance performance = new Performance();
        performance.setName(request.getName());
        performance.setDescription(request.getDescription());
        performance.setGenre(request.getGenre());
        performance.setDuration(request.getDuration());
        performance.setFestival(festival);
        performance.setCreator(requester);

        //Map optional fields 
        if (request.getTechnicalRequirements() != null) {
            TechnicalRequirementFile techFile = new TechnicalRequirementFile();
            techFile.setFilePath(request.getTechnicalRequirements().getFileName());
            techFile.setPerformance(performance); // link back
            performance.setTechnicalRequirement(techFile);
        }

        if (request.getSetlist() != null) {
            performance.setSetlist(request.getSetlist());
        }

        if (request.getMerchandiseItems() != null) {
            Set<MerchandiseItem> merchItems = new HashSet<>();
            for (MerchandiseItemDTO dto : request.getMerchandiseItems()) {
                MerchandiseItem item = new MerchandiseItem();
                item.setName(dto.getName());
                item.setDescription(dto.getDescription());
                item.setType(dto.getType());
                item.setPrice(dto.getPrice());
                item.setPerformance(performance);
                merchItems.add(item);
            }
            performance.setMerchandiseItems(merchItems);
        }

        if (request.getPreferredRehearsalTimes() != null) {
            performance.setPreferredRehearsalTimes(request.getPreferredRehearsalTimes());
        }

        if (request.getPreferredPerformanceSlots() != null) {
            performance.setPreferredPerformanceSlots(request.getPreferredPerformanceSlots());
        }

        //Assign creator as ARTIST 
        FestivalUserRole role = new FestivalUserRole();
        role.setFestival(festival);
        role.setUser(requester);
        role.setRole(FestivalRoleType.ARTIST);
        festivalUserRoleRepository.save(role);

        //Assign additional band members 
        for (Long memberId : request.getBandMemberIds()) {
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new ApiException("Band member not found: " + memberId, HttpStatus.NOT_FOUND));

            // Skip creator if included in the list
            if (member.getId().equals(requester.getId())) {
                continue;
            }

            //add to band members list 
            performance.getBandMembers().add(member);

            //make him an artist for the main festival 
            if (!festivalUserRoleRepository.existsByFestivalAndUserAndRole(performance.getFestival(), member, FestivalRoleType.ARTIST)) {
                FestivalUserRole festivalRole = new FestivalUserRole();
                festivalRole.setFestival(performance.getFestival());
                festivalRole.setUser(member);
                festivalRole.setRole(FestivalRoleType.ARTIST);
                festivalUserRoleRepository.save(festivalRole);
            }
        }
        //Save performance
        Performance savedPerformance = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedPerformance.getId());
        data.put("name", savedPerformance.getName());
        data.put("state", savedPerformance.getState().name());
        data.put("mainArtist", requester.getUsername());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance created successfully",
                data
        );
    }

    /**
     * Updates an existing performance. Only the ARTIST of the performance can
     * update.
     *
     * @param request PerformanceUpdateRequest containing updated fields
     * @return ApiResponse with updated performance details
     * @throws ApiException if requester is not an artist or validation fails
     */
    @Transactional
    public ApiResponse<Map<String, Object>> updatePerformance(PerformanceUpdateRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Verify requester is ARTIST of this performance
        boolean isArtist = performanceRepository.existsByIdAndCreator(performance.getId(), requester);

        if (!isArtist) {
            throw new ApiException("Only ARTIST of this performance can update it", HttpStatus.FORBIDDEN);
        }

        if (performance.getState() != PerformanceState.CREATED) {
            throw new ApiException("Cannot perform performance update once submitted", HttpStatus.FORBIDDEN);
        }

        //Apply updates (only if non-null)
        if (request.getName() != null && !request.getName().isBlank()) {
            boolean nameExists = performanceRepository.existsByFestivalAndNameExcludingPerformance(
                    performance.getFestival(), request.getName(), performance.getId());
            if (nameExists) {
                throw new ApiException("Performance with this name already exists in this festival", HttpStatus.CONFLICT);
            }
            performance.setName(request.getName());
        }

        if (request.getDescription() != null) {
            performance.setDescription(request.getDescription());
        }
        if (request.getGenre() != null) {
            performance.setGenre(request.getGenre());
        }
        if (request.getDuration() != null) {
            performance.setDuration(request.getDuration());
        }

        if (request.getTechnicalRequirements() != null) {
            TechnicalRequirementFile techFile = performance.getTechnicalRequirement();

            if (techFile == null) {
                // Create new TechnicalRequirementFile if none exists
                techFile = new TechnicalRequirementFile();
                techFile.setPerformance(performance);
            }

            techFile.setFilePath(request.getTechnicalRequirements().getFileName());
            performance.setTechnicalRequirement(techFile);
        }

        if (request.getSetlist() != null) {
            performance.setSetlist((Set<String>) request.getSetlist());
        }
        if (request.getMerchandiseItems() != null) {
            Set<MerchandiseItem> merchItems = new HashSet<>();
            for (MerchandiseItemDTO dto : request.getMerchandiseItems()) {
                MerchandiseItem item = new MerchandiseItem();
                item.setName(dto.getName());
                item.setDescription(dto.getDescription());
                item.setType(dto.getType());
                item.setPrice(dto.getPrice());
                item.setPerformance(performance);
                merchItems.add(item);
            }
            performance.setMerchandiseItems(merchItems);
        }

        if (request.getPreferredRehearsalTimes()
                != null) {
            performance.setPreferredRehearsalTimes(request.getPreferredRehearsalTimes());
        }

        if (request.getPreferredPerformanceSlots()
                != null) {
            performance.setPreferredPerformanceSlots(request.getPreferredPerformanceSlots());
        }

        //Update band members if provided
        if (request.getBandMemberIds() != null) {
            for (Long memberId : request.getBandMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new ApiException("Band member not found: " + memberId, HttpStatus.NOT_FOUND));

                // Skip creator if included in the list
                if (member.getId().equals(performance.getCreator().getId())) {
                    continue;
                }

                // Add to performance's bandMembers set
                performance.getBandMembers().add(member);

                // Assign ARTIST role for the festival if not already assigned
                if (!festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                        performance.getFestival(), member, FestivalRoleType.ARTIST)) {
                    FestivalUserRole festivalRole = new FestivalUserRole();
                    festivalRole.setFestival(performance.getFestival());
                    festivalRole.setUser(member);
                    festivalRole.setRole(FestivalRoleType.ARTIST);
                    festivalUserRoleRepository.save(festivalRole);
                }
            }
        }

        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();

        data.put(
                "id", updated.getId());
        data.put(
                "name", updated.getName());
        data.put(
                "genre", updated.getGenre());
        data.put(
                "state", updated.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance updated successfully",
                data
        );
    }

    /**
     * Adds a new band member to an existing performance. Only the ARTIST of the
     * performance can add members.
     *
     * @param request BandMemberAddRequest containing the performance ID and new
     * member username
     * @return ApiResponse confirming addition of the band member
     * @throws ApiException if requester is not artist or user not found
     */
    @Transactional
    public ApiResponse<Map<String, Object>> addBandMember(BandMemberAddRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Verify requester is ARTIST of this performance
        boolean isArtist = performanceRepository.existsByIdAndCreator(performance.getId(), requester);

        if (!isArtist) {
            throw new ApiException("Only ARTIST of this performance can add band members", HttpStatus.FORBIDDEN);
        }

        if (performance.getState() != PerformanceState.CREATED) {
            throw new ApiException("Cannot perform band member addition to performance once submitted", HttpStatus.FORBIDDEN);
        }

        //Find new band member
        User newMember = userRepository.findByUsername(request.getNewMemberUsername())
                .orElseThrow(() -> new ApiException("User to add not found", HttpStatus.NOT_FOUND));

        // Add to performance's bandMembers set
        performance.getBandMembers().add(newMember);

        //Ensure the user is also ARTIST of the festival
        boolean isFestivalArtist = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                performance.getFestival(), newMember, FestivalRoleType.ARTIST);

        if (!isFestivalArtist) {
            FestivalUserRole festivalRole = new FestivalUserRole();
            festivalRole.setFestival(performance.getFestival());
            festivalRole.setUser(newMember);
            festivalRole.setRole(FestivalRoleType.ARTIST);
            festivalUserRoleRepository.save(festivalRole);
        }

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("performanceId", performance.getId());
        data.put("bandMemberAdded", newMember.getUsername());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Band member added successfully",
                data
        );
    }

    /**
     * Submits a performance for festival consideration. Only ARTIST can submit,
     * and festival must be in SUBMISSION state.
     *
     * @param request PerformanceSubmitRequest containing performance ID
     * @return ApiResponse with submitted performance details
     * @throws ApiException if performance incomplete or festival not accepting
     * submissions
     */
    @Transactional
    public ApiResponse<Map<String, Object>> submitPerformance(PerformanceSubmitRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Verify requester is ARTIST of this performance
        boolean isArtist = performanceRepository.existsByIdAndCreator(performance.getId(), requester);

        if (!isArtist) {
            throw new ApiException("Only ARTIST of this performance can submit it", HttpStatus.FORBIDDEN);
        }

        //Check if festival allows submission (example: festival must be in SUBMISSION state)
        if (!performance.getFestival().getState().name().equals("SUBMISSION")) {
            throw new ApiException("Festival is not accepting submissions currently", HttpStatus.BAD_REQUEST);
        }

        //Check that performance details are complete
        if (performance.getName() == null || performance.getName().isBlank()
                || performance.getDescription() == null || performance.getDescription().isBlank()
                || performance.getGenre() == null || performance.getGenre().isBlank()
                || performance.getDuration() == null
                || performance.getTechnicalRequirement() == null
                || performance.getSetlist() == null || performance.getSetlist().isEmpty()
                || performance.getMerchandiseItems() == null || performance.getMerchandiseItems().isEmpty()
                || performance.getPreferredRehearsalTimes() == null || performance.getPreferredRehearsalTimes().isEmpty()
                || performance.getPreferredPerformanceSlots() == null || performance.getPreferredPerformanceSlots().isEmpty()) {
            throw new ApiException("Performance is incomplete and cannot be submitted", HttpStatus.BAD_REQUEST);
        }

        //Change state to SUBMITTED
        performance.setState(Performance.PerformanceState.SUBMITTED);
        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", updated.getId());
        data.put("name", updated.getName());
        data.put("state", updated.getState().name());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance submitted successfully",
                data
        );
    }

    /**
     * Withdraws a performance before submission. Only ARTIST can withdraw and
     * performance must be in CREATED state.
     *
     * @param request PerformanceWithdrawRequest containing performance ID
     * @return ApiResponse confirming withdrawal
     * @throws ApiException if performance already submitted or requester not
     * artist
     */
    @Transactional
    public ApiResponse<Map<String, Object>> withdrawPerformance(PerformanceWithdrawRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Verify requester is ARTIST of this performance
        boolean isArtist = performanceRepository.existsByIdAndCreator(performance.getId(), requester);

        if (!isArtist) {
            throw new ApiException("Only ARTIST of this performance can update it", HttpStatus.FORBIDDEN);
        }

        //Ensure performance has not been submitted yet
        if (performance.getState() != Performance.PerformanceState.CREATED) {
            throw new ApiException("Performance cannot be withdrawn after submission", HttpStatus.BAD_REQUEST);
        }

        //Delete performance
        performanceRepository.delete(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", performance.getId());
        data.put("name", performance.getName());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance withdrawn successfully",
                data
        );
    }

    /**
     * Assigns a STAFF member to a performance. Only an ORGANIZER of the
     * festival can assign staff.
     *
     * @param request PerformanceAssignStaffRequest containing staff ID and
     * performance ID
     * @return ApiResponse confirming assigned staff
     * @throws ApiException if requester not organizer or festival not in
     * ASSIGNMENT state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> assignStaffToPerformance(PerformanceAssignStaffRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //check if the requester is the organizer for that festival that the performance belongs to 
        boolean isOrganizer = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                performance.getFestival(),
                requester,
                FestivalRoleType.ORGANIZER
        );

        if (!isOrganizer) {
            throw new ApiException(
                    "Only an ORGANIZER of this festival can perform this action",
                    HttpStatus.FORBIDDEN
            );
        }

        //Ensure festival is in ASSIGNMENT state
        if (!performance.getFestival().getState().name().equals("ASSIGNMENT")) {
            throw new ApiException("Staff can only be assigned during ASSIGNMENT state", HttpStatus.BAD_REQUEST);
        }

        //Find staff member
        User staff = userRepository.findById(request.getStaffUserId())
                .orElseThrow(() -> new ApiException("Staff user not found", HttpStatus.NOT_FOUND));

        //Ensure staff is registered for this festival
        boolean isFestivalStaff = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                performance.getFestival(), staff, com.festivalmanager.enums.FestivalRoleType.STAFF);
        if (!isFestivalStaff) {
            throw new ApiException("User is not registered as STAFF for this festival", HttpStatus.BAD_REQUEST);
        }

        //Assign staff as primary handler
        performance.setStageManager(staff);
        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("performanceId", updated.getId());
        data.put("performanceName", updated.getName());
        data.put("assignedStaff", staff.getUsername());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Staff assigned to performance successfully",
                data
        );
    }

    /**
     * Reviews a performance. Only the assigned STAFF can review, and festival
     * must be in REVIEW state.
     *
     * @param request PerformanceReviewRequest containing score and comments
     * @return ApiResponse with review details
     * @throws ApiException if requester not assigned staff or invalid
     * score/comments
     */
    @Transactional
    public ApiResponse<Map<String, Object>> reviewPerformance(PerformanceReviewRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Ensure requester is assigned STAFF for this performance
        if (performance.getStageManager() == null || !performance.getStageManager().getId().equals(requester.getId())) {
            throw new ApiException("Only the assigned STAFF member can review this performance", HttpStatus.FORBIDDEN);
        }

        //Ensure festival is in REVIEW state
        if (!performance.getFestival().getState().name().equals("REVIEW")) {
            throw new ApiException("Performance can only be reviewed when the festival is in REVIEW state", HttpStatus.BAD_REQUEST);
        }

        //Validate score and comments
        if (request.getScore() == null || request.getScore() < 0 || request.getScore() > 10) {
            throw new ApiException("Score must be between 0 and 10", HttpStatus.BAD_REQUEST);
        }
        if (request.getReviewerComments() == null || request.getReviewerComments().isBlank()) {
            throw new ApiException("Reviewer comments cannot be empty", HttpStatus.BAD_REQUEST);
        }

        //Update performance
        performance.setScore(request.getScore());
        performance.setReviewerComments(request.getReviewerComments());
        performance.setState(Performance.PerformanceState.REVIEWED);
        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("performanceId", updated.getId());
        data.put("performanceName", updated.getName());
        data.put("score", updated.getScore());
        data.put("reviewerComments", updated.getReviewerComments());
        data.put("state", updated.getState().name());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance reviewed successfully",
                data
        );
    }

    /**
     * Approves a performance. Only an ORGANIZER of the festival can approve,
     * and festival must be in SCHEDULING state.
     *
     * @param request PerformanceApprovalRequest containing performance ID
     * @return ApiResponse confirming approval
     * @throws ApiException if requester not organizer or invalid festival state
     */
    @Transactional
    public ApiResponse<Map<String, Object>> approvePerformance(PerformanceApprovalRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Ensure requester is ORGANIZER of the festival
        boolean isOrganizer = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                performance.getFestival(), requester, com.festivalmanager.enums.FestivalRoleType.ORGANIZER);
        if (!isOrganizer) {
            throw new ApiException("Only an organizer of the festival can approve this performance", HttpStatus.FORBIDDEN);
        }

        //Ensure festival is in SCHEDULING state
        if (!performance.getFestival().getState().name().equals("SCHEDULING")) {
            throw new ApiException("Performances can only be approved when the festival is in SCHEDULING state", HttpStatus.BAD_REQUEST);
        }

        //Update performance state to APPROVED
        performance.setState(Performance.PerformanceState.APPROVED);
        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("performanceId", updated.getId());
        data.put("performanceName", updated.getName());
        data.put("state", updated.getState().name());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance approved successfully",
                data
        );
    }

    /**
     * Rejects a performance manually. Only an ORGANIZER of the festival can
     * reject, and festival must be in SCHEDULING or DECISION state.
     *
     * @param request PerformanceRejectionRequest containing rejection reason
     * @return ApiResponse confirming rejection
     * @throws ApiException if requester not organizer, invalid state, or reason
     * missing
     */
    @Transactional
    public ApiResponse<Map<String, Object>> rejectPerformanceManually(PerformanceRejectionRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Ensure requester is ORGANIZER of the festival
        boolean isOrganizer = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                performance.getFestival(), requester, com.festivalmanager.enums.FestivalRoleType.ORGANIZER);
        if (!isOrganizer) {
            throw new ApiException("Only an organizer of the festival can approve this performance", HttpStatus.FORBIDDEN);
        }

        //Ensure festival is in SCHEDULING or DECISION state
        String festivalState = performance.getFestival().getState().name();
        if (!festivalState.equals("SCHEDULING") && !festivalState.equals("DECISION")) {
            throw new ApiException("Performance can only be rejected during SCHEDULING or DECISION state", HttpStatus.BAD_REQUEST);
        }

        //Ensure rejection reason is provided
        if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
            throw new ApiException("Rejection reason must be provided", HttpStatus.BAD_REQUEST);
        }

        //Update performance state to REJECTED and store reason
        performance.setState(Performance.PerformanceState.REJECTED);
        performance.setReviewerComments(request.getRejectionReason()); // store reason in reviewerComments or a dedicated field
        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("performanceId", updated.getId());
        data.put("performanceName", updated.getName());
        data.put("state", updated.getState().name());
        data.put("rejectionReason", request.getRejectionReason());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance rejected manually",
                data
        );
    }

    /**
     * Submits final performance details (setlist, rehearsal, and performance
     * slots). Only ARTIST can submit final, and festival must be in
     * FINAL_SUBMISSION state.
     *
     * @param request PerformanceFinalSubmissionRequest containing final details
     * @return ApiResponse confirming final submission
     * @throws ApiException if requester not artist or mandatory fields missing
     */
    @Transactional
    public ApiResponse<Map<String, Object>> submitFinalPerformance(PerformanceFinalSubmissionRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Verify requester is ARTIST of this performance
        boolean isArtist = performanceRepository.existsByIdAndCreator(performance.getId(), requester);

        if (!isArtist) {
            throw new ApiException("Only ARTIST of this performance can update it", HttpStatus.FORBIDDEN);
        }

        //Ensure festival is in FINAL_SUBMISSION state
        String festivalState = performance.getFestival().getState().name();
        if (!festivalState.equals("FINAL_SUBMISSION")) {
            throw new ApiException("Final submission is only allowed during FINAL_SUBMISSION festival state", HttpStatus.BAD_REQUEST);
        }

        //Ensure mandatory fields are provided
        if (request.getSetlist() == null || request.getSetlist().isEmpty()) {
            throw new ApiException("Setlist must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getRehearsalTimes() == null || request.getRehearsalTimes().isEmpty()) {
            throw new ApiException("Rehearsal times must be provided", HttpStatus.BAD_REQUEST);
        }
        if (request.getPerformanceTimeSlots() == null || request.getPerformanceTimeSlots().isEmpty()) {
            throw new ApiException("Performance time slots must be provided", HttpStatus.BAD_REQUEST);
        }

        //Update performance details
        performance.setSetlist(request.getSetlist());
        performance.setPreferredRehearsalTimes(request.getRehearsalTimes());
        performance.setPreferredPerformanceSlots(request.getPerformanceTimeSlots());
        performance.setFinal_submitted(true);

        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("performanceId", updated.getId());
        data.put("performanceName", updated.getName());
        data.put("state", updated.getState().name());
        data.put("setlist", updated.getSetlist());
        data.put("rehearsalTimes", updated.getPreferredRehearsalTimes());
        data.put("performanceTimeSlots", updated.getPreferredPerformanceSlots());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance final submission successful",
                data
        );
    }

    /**
     * Accepts a performance after DECISION stage. Only ORGANIZER can accept,
     * and festival must be in DECISION state.
     *
     * @param request PerformanceAcceptanceRequest containing performance ID
     * @return ApiResponse confirming acceptance
     * @throws ApiException if requester not organizer or festival not in
     * DECISION
     */
    @Transactional
    public ApiResponse<Map<String, Object>> acceptPerformance(PerformanceAcceptanceRequest request) {
        // Validate requester 
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        //Ensure requester is ORGANIZER of the festival
        boolean isOrganizer = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                performance.getFestival(), requester, com.festivalmanager.enums.FestivalRoleType.ORGANIZER);
        if (!isOrganizer) {
            throw new ApiException("Only an organizer of the festival can approve this performance", HttpStatus.FORBIDDEN);
        }

        //Ensure festival is in FINAL_SUBMISSION state
        String festivalState = performance.getFestival().getState().name();
        if (!festivalState.equals("DECISION")) {
            throw new ApiException("Performance acceptance is only allowed during DECISION festival state", HttpStatus.BAD_REQUEST);
        }

        performance.setState(Performance.PerformanceState.SCHEDULED);

        Performance updated = performanceRepository.save(performance);

        //Build response
        Map<String, Object> data = new HashMap<>();
        data.put("performanceId", updated.getId());
        data.put("performanceName", updated.getName());
        data.put("state", updated.getState().name());

        return new ApiResponse<>(
                java.time.LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance accepted/scheduled successfully",
                data
        );
    }

    /**
     * Retrieves performance details. Details vary depending on the role of the
     * requester (creator, band member, staff, organizer, visitor).
     *
     * @param request PerformanceViewRequest containing performance ID and
     * optional requester info
     * @return ApiResponse with performance details
     * @throws ApiException if performance not found or invalid requester
     */
    @Transactional
    public ApiResponse<Map<String, Object>> viewPerformance(PerformanceViewRequest request) {

        //Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        User requester = null;

        if (request.getRequesterUsername() != null && !request.getRequesterUsername().isEmpty()) {
            // Validate requester if username + token provided
            requester = userSecurityService.validateRequester(
                    request.getRequesterUsername(),
                    request.getToken()
            );
        }

        // Map to view based on role
        PerformanceSearchResponseDTO dto = mapPerformance(performance, requester);

        Map<String, Object> data = new HashMap<>();
        data.put("performance", dto);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance retrieved successfully",
                data
        );
    }

    //-------------------- SEARCH PERFORMANCE --------------------
    /**
     * Searches for performances based on name, artists, and genre. If no
     * criteria are provided, all performances are returned. Results are sorted
     * by genre, then by name.
     *
     * @param request PerformanceSearchRequest containing search criteria
     * @return ApiResponse with a list of matching performances
     */
    @Transactional
    public ApiResponse<Map<String, Object>> searchPerformances(PerformanceSearchRequest request) {

        List<Performance> performances = performanceRepository.findAll();
        User requester = null;

        if (request.getRequesterUsername() != null && !request.getRequesterUsername().isEmpty()) {
            // Validate requester if username provided
            requester = userSecurityService.validateRequester(
                    request.getRequesterUsername(),
                    request.getToken()
            );
        }

        // --- FILTER BY NAME ---
        if (request.getName() != null && !request.getName().isBlank()) {
            String[] words = request.getName().trim().split("\\s+");
            performances = performances.stream()
                    .filter(p -> Arrays.stream(words)
                    .allMatch(word -> p.getName().toLowerCase().contains(word.toLowerCase())))
                    .toList();
        }

        // --- FILTER BY GENRE ---
        if (request.getGenre() != null && !request.getGenre().isBlank()) {
            String[] words = request.getGenre().trim().split("\\s+");
            performances = performances.stream()
                    .filter(p -> Arrays.stream(words)
                    .allMatch(word -> p.getGenre().toLowerCase().contains(word.toLowerCase())))
                    .toList();
        }

        // --- FILTER BY ARTIST (creator or band members) ---
        if (request.getArtist() != null && !request.getArtist().isBlank()) {
            String[] words = request.getArtist().trim().split("\\s+");
            performances = performances.stream()
                    .filter(p -> {
                        String creatorName = p.getCreator().getUsername().toLowerCase();
                        Set<String> bandUsernames = p.getBandMembers().stream()
                                .map(u -> u.getUsername().toLowerCase())
                                .collect(Collectors.toSet());

                        // All words must match either in creator or in at least one band member
                        return Arrays.stream(words).allMatch(word
                                -> creatorName.contains(word.toLowerCase())
                                || bandUsernames.stream().anyMatch(b -> b.contains(word.toLowerCase()))
                        );
                    })
                    .toList();
        }

        // --- ROLE FILTERING ---
        if (requester == null) {
            // Visitors can only see scheduled performances
            performances = performances.stream()
                    .filter(p -> p.getState() == Performance.PerformanceState.SCHEDULED)
                    .toList();
        }

        // --- SORTING (genre → name) ---
        performances = performances.stream()
                .sorted(Comparator
                        .comparing(Performance::getGenre, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Performance::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        User finalRequester = requester;

        List<PerformanceSearchResponseDTO> results = performances.stream()
                .map(p -> mapPerformance(p, finalRequester)) // applies role-based detail logic
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("performances", results);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performances retrieved successfully",
                data
        );
    }

    private PerformanceSearchResponseDTO mapPerformance(Performance performance, User requester) {
        PerformanceSearchResponseDTO dto = new PerformanceSearchResponseDTO();

        // --- BASIC FIELDS (always visible to VISITOR and above if performance is SCHEDULED) ---
        dto.setId(performance.getId());
        dto.setName(performance.getName());
        dto.setGenre(performance.getGenre());
        dto.setDuration(performance.getDuration());
        dto.setState(performance.getState().name());
        dto.setFestivalId(performance.getFestival().getId());

        // Visitors can only see scheduled performances
        if (requester == null && performance.getState() != Performance.PerformanceState.SCHEDULED) {
            return dto; // only basic info returned
        }

        // --- ROLE-BASED ACCESS CONTROL ---
        boolean isCreator = requester != null && performance.getCreator().equals(requester);
        boolean isBandMember = requester != null && performance.getBandMembers().contains(requester);
        boolean isStageManager = requester != null && performance.getStageManager() != null
                && performance.getStageManager().equals(requester);
        boolean isOrganizer = requester != null && performance.getFestival().getUserRoles().stream()
                .anyMatch(r -> r.getUser().equals(requester) && r.getRole() == FestivalRoleType.ORGANIZER);

        boolean canViewFullDetails = isCreator || isBandMember || isStageManager || isOrganizer;

        if (canViewFullDetails) {
            dto.setDescription(performance.getDescription());
            dto.setCreator(performance.getCreator().getUsername());

            // Band members
            dto.setBandMembers(performance.getBandMembers().stream()
                    .map(User::getUsername)
                    .toList());

            // Tech requirements
            if (performance.getTechnicalRequirement() != null) {
                TechnicalRequirementDTO trDTO = new TechnicalRequirementDTO();
                trDTO.setFileName(performance.getTechnicalRequirement().getFilePath());
                dto.setTechnicalRequirement(trDTO);
            }

            // Setlist
            dto.setSetlist(performance.getSetlist());

            // Merchandise
            dto.setMerchandiseItems(performance.getMerchandiseItems().stream()
                    .map(m -> {
                        MerchandiseItemDTO mDTO = new MerchandiseItemDTO();
                        mDTO.setName(m.getName());
                        mDTO.setDescription(m.getDescription());
                        mDTO.setType(m.getType());
                        mDTO.setPrice(m.getPrice());
                        return mDTO;
                    })
                    .toList());

            // Times
            dto.setPreferredRehearsalTimes(performance.getPreferredRehearsalTimes());
            dto.setPreferredPerformanceSlots(performance.getPreferredPerformanceSlots());

            // Stage manager
            if (performance.getStageManager() != null) {
                dto.setStageManager(performance.getStageManager().getUsername());
            }

            // Reviewer details
            dto.setReviewerComments(performance.getReviewerComments());
            dto.setScore(performance.getScore());
        }

        return dto;
    }

}
