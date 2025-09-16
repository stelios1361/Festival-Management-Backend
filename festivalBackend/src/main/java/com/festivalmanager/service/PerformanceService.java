package com.festivalmanager.service;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.performance.MerchandiseItemDTO;
import com.festivalmanager.dto.performance.PerformanceApprovalRequest;
import com.festivalmanager.dto.performance.PerformanceAssignStaffRequest;
import com.festivalmanager.dto.performance.PerformanceCreateRequest;
import com.festivalmanager.dto.performance.PerformanceFinalSubmissionRequest;
import com.festivalmanager.dto.performance.PerformanceRejectionRequest;
import com.festivalmanager.dto.performance.PerformanceReviewRequest;
import com.festivalmanager.dto.performance.PerformanceWithdrawRequest;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.Festival;
import com.festivalmanager.model.MerchandiseItem;
import com.festivalmanager.model.Performance;
import com.festivalmanager.model.TechnicalRequirementFile;
import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.FestivalRepository;
import com.festivalmanager.repository.FestivalUserRoleRepository;
import com.festivalmanager.repository.PerformanceRepository;
import com.festivalmanager.repository.TokenRepository;
import com.festivalmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PerformanceService {
//
//    @Autowired
//    private PerformanceRepository performanceRepository;
//
//    @Autowired
//    private FestivalRepository festivalRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TokenRepository tokenRepository;
//
//
//    @Autowired
//    private FestivalUserRoleRepository festivalUserRoleRepository;
//
//    @Transactional
//    public ApiResponse<Map<String, Object>> createPerformance(PerformanceCreateRequest request) {
//        // ---------------- 1. Validate token ----------------
//        Token token = tokenRepository.findByValue(request.getToken())
//                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
//
//        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // ---------------- 2. Find requesting user ----------------
//        User requester = userRepository.findByUsername(request.getRequesterUsername())
//                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));
//
//        if (!requester.isActive()) {
//            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
//        }
//
//        // ---------------- 3. Find festival ----------------
//        Festival festival = festivalRepository.findById(request.getFestivalId())
//                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));
//
//        // ---------------- 4. Check performance name uniqueness within festival ----------------
//        boolean nameExists = festival.getPerformances().stream()
//                .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));
//        if (nameExists) {
//            throw new ApiException("Performance with this name already exists in this festival", HttpStatus.CONFLICT);
//        }
//
//        // ---------------- 5. Validate required fields ----------------
//        if (request.getGenre() == null || request.getGenre().isBlank()) {
//            throw new ApiException("Genre must be provided", HttpStatus.BAD_REQUEST);
//        }
//        if (request.getDuration() == null || request.getDuration() <= 0) {
//            throw new ApiException("Valid duration must be provided", HttpStatus.BAD_REQUEST);
//        }
//        if (request.getName() == null || request.getName().isBlank()) {
//            throw new ApiException("Name must be provided", HttpStatus.BAD_REQUEST);
//        }
//        if (request.getDescription() == null || request.getDescription().isBlank()) {
//            throw new ApiException("Description must be provided", HttpStatus.BAD_REQUEST);
//        }
//        if (request.getBandMemberIds() == null || request.getBandMemberIds().isEmpty()) {
//            throw new ApiException("At least one band member must be provided", HttpStatus.BAD_REQUEST);
//        }
//
//        // ---------------- 6. Create performance entity ----------------
//        Performance performance = new Performance();
//        performance.setName(request.getName());
//        performance.setDescription(request.getDescription());
//        performance.setGenre(request.getGenre());
//        performance.setDuration(request.getDuration());
//        performance.setFestival(festival);
//
//// ---------------- 7. Map optional fields ----------------
//        if (request.getTechnicalRequirements() != null) {
//            TechnicalRequirementFile techFile = new TechnicalRequirementFile();
//            techFile.setFilePath(request.getTechnicalRequirements().getFileName());
//            techFile.setPerformance(performance); // link back
//            performance.setTechnicalRequirement(techFile);
//        }
//
//        if (request.getSetlist() != null) {
//            performance.setSetlist(request.getSetlist());
//        }
//
//        if (request.getMerchandiseItems() != null) {
//            Set<MerchandiseItem> merchItems = new HashSet<>();
//            for (MerchandiseItemDTO dto : request.getMerchandiseItems()) {
//                MerchandiseItem item = new MerchandiseItem();
//                item.setName(dto.getName());
//                item.setDescription(dto.getDescription());
//                item.setType(dto.getType());
//                item.setPrice(dto.getPrice());
//                item.setPerformance(performance); // link back
//                merchItems.add(item);
//            }
//            performance.setMerchandiseItems(merchItems);
//        }
//
//        if (request.getPreferredRehearsalTimes() != null) {
//            performance.setPreferredRehearsalTimes(request.getPreferredRehearsalTimes());
//        }
//
//        if (request.getPreferredPerformanceSlots() != null) {
//            performance.setPreferredPerformanceSlots(request.getPreferredPerformanceSlots());
//        }
//
//        // ---------------- 8. Save performance ----------------
//        Performance savedPerformance = performanceRepository.save(performance);
//
//        // ---------------- 9. Assign creator as main ARTIST ----------------
//        PerformanceUserRole creatorRole = new PerformanceUserRole();
//        creatorRole.setPerformance(savedPerformance);
//        creatorRole.setUser(requester);
//        creatorRole.setRole(PerformanceRoleType.ARTIST);
//        creatorRole.setMainArtist(true); // mark as main artist
//        performanceUserRoleRepository.save(creatorRole);
//
//        // ---------------- 10. Assign additional band members ----------------
//        for (Long memberId : request.getBandMemberIds()) {
//            User member = userRepository.findById(memberId)
//                    .orElseThrow(() -> new ApiException("Band member not found: " + memberId, HttpStatus.NOT_FOUND));
//
//            // Skip creator if included in the list
//            if (member.getId().equals(requester.getId())) {
//                continue;
//            }
//
//            PerformanceUserRole role = new PerformanceUserRole();
//            role.setPerformance(savedPerformance);
//            role.setUser(member);
//            role.setRole(PerformanceRoleType.ARTIST);
//            role.setMainArtist(false);
//            performanceUserRoleRepository.save(role);
//        }
//
//        // ---------------- 11. Build response ----------------
//        Map<String, Object> data = new HashMap<>();
//        data.put("id", savedPerformance.getId());
//        data.put("identifier", savedPerformance.getIdentifier());
//        data.put("name", savedPerformance.getName());
//        data.put("state", savedPerformance.getState().name());
//        data.put("mainArtist", requester.getUsername());
//
//        return new ApiResponse<>(
//                LocalDateTime.now(),
//                HttpStatus.OK.value(),
//                "Performance created successfully",
//                data
//        );
//    }
//
////    @Transactional
////    public ApiResponse<Map<String, Object>> updatePerformance(PerformanceUpdateRequest request) {
////        // 1. Validate token
////        Token token = tokenRepository.findByValue(request.getToken())
////                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
////
////        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
////            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
////        }
////
////        // 2. Find requester
////        User requester = userRepository.findByUsername(request.getRequesterUsername())
////                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
////
////        // 3. Find performance
////        Performance performance = performanceRepository.findById(request.getPerformanceId())
////                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
////
////        // 4. Verify requester is ARTIST of this performance
////        boolean isArtist = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
////                performance, requester, PerformanceRoleType.ARTIST);
////
////        if (!isArtist) {
////            throw new ApiException("Only ARTISTS of this performance can update it", HttpStatus.FORBIDDEN);
////        }
////
////        // 5. Apply updates (only if non-null)
////        if (request.getName() != null && !request.getName().isBlank()) {
////            // Ensure uniqueness within the festival
////            boolean nameExists = performanceRepository.existsByFestivalAndName(
////                    performance.getFestival(), request.getName());
////            if (nameExists && !performance.getName().equals(request.getName())) {
////                throw new ApiException("Performance with this name already exists in this festival", HttpStatus.CONFLICT);
////            }
////            performance.setName(request.getName());
////        }
////
////        if (request.getDescription() != null) {
////            performance.setDescription(request.getDescription());
////        }
////        if (request.getGenre() != null) {
////            performance.setGenre(request.getGenre());
////        }
////        if (request.getDuration() != null) {
////            performance.setDuration(request.getDuration());
////        }
////
////        if (request.getTechnicalRequirements() != null) {
////            performance.setTechnicalRequirements(request.getTechnicalRequirements());
////        }
////        if (request.getSetlist() != null) {
////            performance.setSetlist((Set<String>) request.getSetlist());
////        }
////        if (request.getMerchandiseItems() != null) {
////            performance.setMerchandiseItems((Set<Performance.MerchandiseItem>) request.getMerchandiseItems());
////        }
////        if (request.getPreferredRehearsalTimes() != null) {
////            performance.setPreferredRehearsalTimes(request.getPreferredRehearsalTimes());
////        }
////        if (request.getPreferredPerformanceSlots() != null) {
////            performance.setPreferredPerformanceSlots(request.getPreferredPerformanceSlots());
////        }
////
////        // 6. Update band members if provided
////        if (request.getBandMemberIds() != null) {
////            List<User> bandMembers = userRepository.findAllById(request.getBandMemberIds());
////            for (User member : bandMembers) {
////                boolean alreadyMember = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
////                        performance, member, PerformanceRoleType.BAND_MEMBER);
////                if (!alreadyMember) {
////                    PerformanceUserRole role = new PerformanceUserRole();
////                    role.setPerformance(performance);
////                    role.setUser(member);
////                    role.setRole(PerformanceRoleType.BAND_MEMBER);
////                    performanceUserRoleRepository.save(role);
////                }
////            }
////        }
////
////        Performance updated = performanceRepository.save(performance);
////
////        // 7. Build response
////        Map<String, Object> data = new HashMap<>();
////        data.put("id", updated.getId());
////        data.put("name", updated.getName());
////        data.put("genre", updated.getGenre());
////        data.put("state", updated.getState().name());
////
////        return new ApiResponse<>(
////                LocalDateTime.now(),
////                HttpStatus.OK.value(),
////                "Performance updated successfully",
////                data
////        );
////    }
////    @Transactional
////    public ApiResponse<Map<String, Object>> addBandMember(BandMemberAddRequest request) {
////        // 1. Validate token
////        Token token = tokenRepository.findByValue(request.getToken())
////                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
////
////        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
////            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
////        }
////
////        // 2. Find requester
////        User requester = userRepository.findByUsername(request.getRequesterUsername())
////                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
////
////        // 3. Find performance
////        Performance performance = performanceRepository.findById(request.getPerformanceId())
////                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
////
////        // 4. Ensure requester is MAIN ARTIST (creator) of performance
////        boolean isMainArtist = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
////                performance, requester, PerformanceRoleType.ARTIST);
////
////        if (!isMainArtist) {
////            throw new ApiException("Only the main artist can add band members", HttpStatus.FORBIDDEN);
////        }
////
////        // 5. Find new band member
////        User newMember = userRepository.findByUsername(request.getNewMemberUsername())
////                .orElseThrow(() -> new ApiException("User to add not found", HttpStatus.NOT_FOUND));
////
////        // 6. Ensure they are not already a band member
////        boolean alreadyMember = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
////                performance, newMember, PerformanceRoleType.BAND_MEMBER);
////
////        if (alreadyMember) {
////            throw new ApiException("This user is already a band member", HttpStatus.CONFLICT);
////        }
////
////        // 7. Add as BAND_MEMBER to performance
////        PerformanceUserRole bandRole = new PerformanceUserRole();
////        bandRole.setPerformance(performance);
////        bandRole.setUser(newMember);
////        bandRole.setRole(PerformanceRoleType.BAND_MEMBER);
////        performanceUserRoleRepository.save(bandRole);
////
////        // 8. Ensure the user is also ARTIST of the festival
////        boolean isFestivalArtist = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
////                performance.getFestival(), newMember, FestivalRoleType.ARTIST);
////
////        if (!isFestivalArtist) {
////            FestivalUserRole festivalRole = new FestivalUserRole();
////            festivalRole.setFestival(performance.getFestival());
////            festivalRole.setUser(newMember);
////            festivalRole.setRole(PerformanceRoleType.ARTIST);
////            festivalUserRoleRepository.save(festivalRole);
////        }
////
////        // 9. Build response
////        Map<String, Object> data = new HashMap<>();
////        data.put("performanceId", performance.getId());
////        data.put("bandMemberAdded", newMember.getUsername());
////
////        return new ApiResponse<>(
////                LocalDateTime.now(),
////                HttpStatus.OK.value(),
////                "Band member added successfully",
////                data
////        );
////    }
////    @Transactional
////    public ApiResponse<Map<String, Object>> submitPerformance(PerformanceSubmitRequest request) {
////        // 1. Validate token
////        Token token = tokenRepository.findByValue(request.getToken())
////                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
////        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
////            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
////        }
////
////        // 2. Find requester
////        User requester = userRepository.findByUsername(request.getRequesterUsername())
////                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
////
////        // 3. Find performance
////        Performance performance = performanceRepository.findById(request.getPerformanceId())
////                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
////
////        // 4. Ensure requester is ARTIST of this performance
////        boolean isArtist = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
////                performance, requester, PerformanceRoleType.ARTIST);
////        if (!isArtist) {
////            throw new ApiException("Only ARTISTS of this performance can submit it", HttpStatus.FORBIDDEN);
////        }
////
////        // 5. Check if festival allows submission (example: festival must be in SUBMISSION state)
////        if (!performance.getFestival().getState().name().equals("SUBMISSION")) {
////            throw new ApiException("Festival is not accepting submissions currently", HttpStatus.BAD_REQUEST);
////        }
////
////        // 6. Check that performance details are complete
////        if (performance.getName() == null || performance.getName().isBlank()
////                || performance.getDescription() == null || performance.getDescription().isBlank()
////                || performance.getGenre() == null || performance.getGenre().isBlank()
////                || performance.getDuration() == null
////                || performance.getUserRoles() == null || performance.getUserRoles().isEmpty()
////                || performance.getTechnicalRequirements() == null
////                || performance.getSetlist() == null || performance.getSetlist().isEmpty()
////                || performance.getMerchandiseItems() == null || performance.getMerchandiseItems().isEmpty()
////                || performance.getPreferredRehearsalTimes() == null || performance.getPreferredRehearsalTimes().isEmpty()
////                || performance.getPreferredPerformanceSlots() == null || performance.getPreferredPerformanceSlots().isEmpty()) {
////            throw new ApiException("Performance is incomplete and cannot be submitted", HttpStatus.BAD_REQUEST);
////        }
////
////        // 7. Change state to SUBMITTED
////        performance.setState(Performance.PerformanceState.SUBMITTED);
////        Performance updated = performanceRepository.save(performance);
////
////        // 8. Build response
////        Map<String, Object> data = new HashMap<>();
////        data.put("id", updated.getId());
////        data.put("identifier", updated.getIdentifier());
////        data.put("name", updated.getName());
////        data.put("state", updated.getState().name());
////
////        return new ApiResponse<>(
////                java.time.LocalDateTime.now(),
////                HttpStatus.OK.value(),
////                "Performance submitted successfully",
////                data
////        );
////    }
//    @Transactional
//    public ApiResponse<Map<String, Object>> withdrawPerformance(PerformanceWithdrawRequest request) {
//        // 1. Validate token
//        Token token = tokenRepository.findByValue(request.getToken())
//                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
//        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // 2. Find requester
//        User requester = userRepository.findByUsername(request.getRequesterUsername())
//                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
//
//        // 3. Find performance
//        Performance performance = performanceRepository.findById(request.getPerformanceId())
//                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
//
//        // 4. Ensure requester is ARTIST of this performance
//        boolean isArtist = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
//                performance, requester, PerformanceRoleType.ARTIST);
//        if (!isArtist) {
//            throw new ApiException("Only ARTISTS of this performance can withdraw it", HttpStatus.FORBIDDEN);
//        }
//
//        // 5. Ensure performance has not been submitted yet
//        if (performance.getState() != Performance.PerformanceState.CREATED) {
//            throw new ApiException("Performance cannot be withdrawn after submission", HttpStatus.BAD_REQUEST);
//        }
//
//        // 6. Delete performance
//        performanceRepository.delete(performance);
//
//        // 7. Build response
//        Map<String, Object> data = new HashMap<>();
//        data.put("id", performance.getId());
//        data.put("name", performance.getName());
//
//        return new ApiResponse<>(
//                java.time.LocalDateTime.now(),
//                HttpStatus.OK.value(),
//                "Performance withdrawn successfully",
//                data
//        );
//    }
//
//    @Transactional
//    public ApiResponse<Map<String, Object>> assignStaffToPerformance(PerformanceAssignStaffRequest request) {
//        // 1. Validate token
//        Token token = tokenRepository.findByValue(request.getToken())
//                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
//        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // 2. Find requester (optional: could check if festival admin)
//        User requester = userRepository.findByUsername(request.getRequesterUsername())
//                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
//
//        // 3. Find performance
//        Performance performance = performanceRepository.findById(request.getPerformanceId())
//                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
//
//        // 4. Ensure festival is in ASSIGNMENT state
//        if (!performance.getFestival().getState().name().equals("ASSIGNMENT")) {
//            throw new ApiException("Staff can only be assigned during ASSIGNMENT state", HttpStatus.BAD_REQUEST);
//        }
//
//        // 5. Find staff member
//        User staff = userRepository.findById(request.getStaffUserId())
//                .orElseThrow(() -> new ApiException("Staff user not found", HttpStatus.NOT_FOUND));
//
//        // 6. Ensure staff is registered for this festival
//        boolean isFestivalStaff = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
//                performance.getFestival(), staff, com.festivalmanager.enums.FestivalRoleType.STAFF);
//        if (!isFestivalStaff) {
//            throw new ApiException("User is not registered as STAFF for this festival", HttpStatus.BAD_REQUEST);
//        }
//
//        // 7. Assign staff as primary handler
//        performance.setStageManager(staff);
//        Performance updated = performanceRepository.save(performance);
//
//        // 8. Build response
//        Map<String, Object> data = new HashMap<>();
//        data.put("performanceId", updated.getId());
//        data.put("performanceName", updated.getName());
//        data.put("assignedStaff", staff.getUsername());
//
//        return new ApiResponse<>(
//                java.time.LocalDateTime.now(),
//                HttpStatus.OK.value(),
//                "Staff assigned to performance successfully",
//                data
//        );
//    }
//
//    @Transactional
//    public ApiResponse<Map<String, Object>> reviewPerformance(PerformanceReviewRequest request) {
//        // 1. Validate token
//        Token token = tokenRepository.findByValue(request.getToken())
//                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
//        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // 2. Find requester (staff)
//        User requester = userRepository.findByUsername(request.getRequesterUsername())
//                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
//
//        // 3. Find performance
//        Performance performance = performanceRepository.findById(request.getPerformanceId())
//                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
//
//        // 4. Ensure requester is assigned STAFF for this performance
//        if (performance.getStageManager() == null || !performance.getStageManager().getId().equals(requester.getId())) {
//            throw new ApiException("Only the assigned STAFF member can review this performance", HttpStatus.FORBIDDEN);
//        }
//
//        // 5. Ensure festival is in REVIEW state
//        if (!performance.getFestival().getState().name().equals("REVIEW")) {
//            throw new ApiException("Performance can only be reviewed when the festival is in REVIEW state", HttpStatus.BAD_REQUEST);
//        }
//
//        // 6. Validate score and comments
//        if (request.getScore() == null || request.getScore() < 0 || request.getScore() > 10) {
//            throw new ApiException("Score must be between 0 and 10", HttpStatus.BAD_REQUEST);
//        }
//        if (request.getReviewerComments() == null || request.getReviewerComments().isBlank()) {
//            throw new ApiException("Reviewer comments cannot be empty", HttpStatus.BAD_REQUEST);
//        }
//
//        // 7. Update performance
//        performance.setScore(request.getScore());
//        performance.setReviewerComments(request.getReviewerComments());
//        performance.setState(Performance.PerformanceState.REVIEWED);
//        Performance updated = performanceRepository.save(performance);
//
//        // 8. Build response
//        Map<String, Object> data = new HashMap<>();
//        data.put("performanceId", updated.getId());
//        data.put("performanceName", updated.getName());
//        data.put("score", updated.getScore());
//        data.put("reviewerComments", updated.getReviewerComments());
//        data.put("state", updated.getState().name());
//
//        return new ApiResponse<>(
//                java.time.LocalDateTime.now(),
//                HttpStatus.OK.value(),
//                "Performance reviewed successfully",
//                data
//        );
//    }
//
//    @Transactional
//    public ApiResponse<Map<String, Object>> approvePerformance(PerformanceApprovalRequest request) {
//        // 1. Validate token
//        Token token = tokenRepository.findByValue(request.getToken())
//                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
//        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // 2. Find requester
//        User requester = userRepository.findByUsername(request.getRequesterUsername())
//                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
//
//        // 3. Find performance
//        Performance performance = performanceRepository.findById(request.getPerformanceId())
//                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
//
//        // 4. Ensure requester is ORGANIZER of the festival
//        boolean isOrganizer = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
//                performance.getFestival(), requester, com.festivalmanager.enums.FestivalRoleType.ORGANIZER);
//        if (!isOrganizer) {
//            throw new ApiException("Only an organizer of the festival can approve this performance", HttpStatus.FORBIDDEN);
//        }
//
//        // 5. Ensure festival is in SCHEDULING state
//        if (!performance.getFestival().getState().name().equals("SCHEDULING")) {
//            throw new ApiException("Performances can only be approved when the festival is in SCHEDULING state", HttpStatus.BAD_REQUEST);
//        }
//
//        // 6. Update performance state to APPROVED
//        performance.setState(Performance.PerformanceState.APPROVED);
//        Performance updated = performanceRepository.save(performance);
//
//        // 7. Build response
//        Map<String, Object> data = new HashMap<>();
//        data.put("performanceId", updated.getId());
//        data.put("performanceName", updated.getName());
//        data.put("state", updated.getState().name());
//
//        return new ApiResponse<>(
//                java.time.LocalDateTime.now(),
//                HttpStatus.OK.value(),
//                "Performance approved successfully",
//                data
//        );
//    }
//
//    @Transactional
//    public ApiResponse<Map<String, Object>> rejectPerformanceManually(PerformanceRejectionRequest request) {
//        // 1. Validate token
//        Token token = tokenRepository.findByValue(request.getToken())
//                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
//        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // 2. Find requester
//        User requester = userRepository.findByUsername(request.getRequesterUsername())
//                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
//
//        // 3. Find performance
//        Performance performance = performanceRepository.findById(request.getPerformanceId())
//                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
//
//        // 4. Ensure requester is ORGANIZER of the festival
//        boolean isOrganizer = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
//                performance.getFestival(), requester, com.festivalmanager.enums.FestivalRoleType.ORGANIZER);
//        if (!isOrganizer) {
//            throw new ApiException("Only an organizer of the festival can reject this performance", HttpStatus.FORBIDDEN);
//        }
//
//        // 5. Ensure festival is in SCHEDULING or DECISION state
//        String festivalState = performance.getFestival().getState().name();
//        if (!festivalState.equals("SCHEDULING") && !festivalState.equals("DECISION")) {
//            throw new ApiException("Performance can only be rejected during SCHEDULING or DECISION state", HttpStatus.BAD_REQUEST);
//        }
//
//        // 6. Ensure rejection reason is provided
//        if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
//            throw new ApiException("Rejection reason must be provided", HttpStatus.BAD_REQUEST);
//        }
//
//        // 7. Update performance state to REJECTED and store reason
//        performance.setState(Performance.PerformanceState.REJECTED);
//        performance.setReviewerComments(request.getRejectionReason()); // store reason in reviewerComments or a dedicated field
//        Performance updated = performanceRepository.save(performance);
//
//        // 8. Build response
//        Map<String, Object> data = new HashMap<>();
//        data.put("performanceId", updated.getId());
//        data.put("performanceName", updated.getName());
//        data.put("state", updated.getState().name());
//        data.put("rejectionReason", request.getRejectionReason());
//
//        return new ApiResponse<>(
//                java.time.LocalDateTime.now(),
//                HttpStatus.OK.value(),
//                "Performance rejected manually",
//                data
//        );
//    }
//
//    @Transactional
//    public ApiResponse<Map<String, Object>> submitFinalPerformance(PerformanceFinalSubmissionRequest request) {
//        // 1. Validate token
//        Token token = tokenRepository.findByValue(request.getToken())
//                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
//        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // 2. Find requester
//        User requester = userRepository.findByUsername(request.getRequesterUsername())
//                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));
//
//        // 3. Find performance
//        Performance performance = performanceRepository.findById(request.getPerformanceId())
//                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));
//
//        // 4. Ensure requester is ARTIST of the performance
//        boolean isArtist = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
//                performance, requester, com.festivalmanager.enums.PerformanceRoleType.ARTIST);
//        if (!isArtist) {
//            throw new ApiException("Only an artist of the performance can submit final details", HttpStatus.FORBIDDEN);
//        }
//
//        // 5. Ensure festival is in FINAL_SUBMISSION state
//        String festivalState = performance.getFestival().getState().name();
//        if (!festivalState.equals("FINAL_SUBMISSION")) {
//            throw new ApiException("Final submission is only allowed during FINAL_SUBMISSION festival state", HttpStatus.BAD_REQUEST);
//        }
//
//        // 6. Ensure mandatory fields are provided
//        if (request.getSetlist() == null || request.getSetlist().isEmpty()) {
//            throw new ApiException("Setlist must be provided", HttpStatus.BAD_REQUEST);
//        }
//        if (request.getRehearsalTimes() == null || request.getRehearsalTimes().isEmpty()) {
//            throw new ApiException("Rehearsal times must be provided", HttpStatus.BAD_REQUEST);
//        }
//        if (request.getPerformanceTimeSlots() == null || request.getPerformanceTimeSlots().isEmpty()) {
//            throw new ApiException("Performance time slots must be provided", HttpStatus.BAD_REQUEST);
//        }
//
//        // 7. Update performance details
//        performance.setSetlist((Set<String>) request.getSetlist());
//        performance.setPreferredRehearsalTimes(request.getRehearsalTimes());
//        performance.setPreferredPerformanceSlots(request.getPerformanceTimeSlots());
//
//        Performance updated = performanceRepository.save(performance);
//
//        // 8. Build response
//        Map<String, Object> data = new HashMap<>();
//        data.put("performanceId", updated.getId());
//        data.put("performanceName", updated.getName());
//        data.put("state", updated.getState().name());
//        data.put("setlist", updated.getSetlist());
//        data.put("rehearsalTimes", updated.getPreferredRehearsalTimes());
//        data.put("performanceTimeSlots", updated.getPreferredPerformanceSlots());
//
//        return new ApiResponse<>(
//                java.time.LocalDateTime.now(),
//                HttpStatus.OK.value(),
//                "Performance final submission successful",
//                data
//        );
//    }

}
