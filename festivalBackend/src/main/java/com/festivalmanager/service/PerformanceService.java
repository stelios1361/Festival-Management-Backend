package com.festivalmanager.service;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.performance.BandMemberAddRequest;
import com.festivalmanager.dto.performance.PerformanceCreateRequest;
import com.festivalmanager.dto.performance.PerformanceUpdateRequest;
import com.festivalmanager.enums.FestivalRoleType;
import com.festivalmanager.enums.PerformanceRoleType;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.Festival;
import com.festivalmanager.model.FestivalUserRole;
import com.festivalmanager.model.Performance;
import com.festivalmanager.model.PerformanceUserRole;
import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.FestivalRepository;
import com.festivalmanager.repository.PerformanceRepository;
import com.festivalmanager.repository.PerformanceUserRoleRepository;
import com.festivalmanager.repository.TokenRepository;
import com.festivalmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PerformanceUserRoleRepository performanceUserRoleRepository;

    @Transactional
    public ApiResponse<Map<String, Object>> createPerformance(PerformanceCreateRequest request) {
        // ---------------- 1. Validate token ----------------
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // ---------------- 2. Find requesting user ----------------
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester user not found", HttpStatus.NOT_FOUND));

        if (!requester.isActive()) {
            throw new ApiException("Requester account is deactivated", HttpStatus.FORBIDDEN);
        }

        // ---------------- 3. Find festival ----------------
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new ApiException("Festival not found", HttpStatus.NOT_FOUND));

        // ---------------- 4. Check performance name uniqueness within festival ----------------
        boolean nameExists = festival.getPerformances().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));
        if (nameExists) {
            throw new ApiException("Performance with this name already exists in this festival", HttpStatus.CONFLICT);
        }

        // ---------------- 5. Validate required fields ----------------
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

        // ---------------- 6. Create performance entity ----------------
        Performance performance = new Performance();
        performance.setName(request.getName());
        performance.setDescription(request.getDescription());
        performance.setGenre(request.getGenre());
        performance.setDuration(request.getDuration());
        performance.setFestival(festival);

        // ---------------- 7. Map optional fields ----------------
        if (request.getTechnicalRequirements() != null) {
            Performance.TechnicalRequirements tech = new Performance.TechnicalRequirements();
            tech.setEquipment(request.getTechnicalRequirements().getEquipment());
            tech.setStageSetup(request.getTechnicalRequirements().getStageSetup());
            tech.setSoundLighting(request.getTechnicalRequirements().getSoundLighting());
            performance.setTechnicalRequirements(tech);
        }

        if (request.getSetlist() != null) {
            performance.setSetlist(request.getSetlist());
        }

        if (request.getMerchandiseItems() != null) {
            Set<Performance.MerchandiseItem> merchItems = new HashSet<>();
            for (PerformanceCreateRequest.MerchandiseItemDTO dto : request.getMerchandiseItems()) {
                Performance.MerchandiseItem item = new Performance.MerchandiseItem();
                item.setName(dto.getName());
                item.setDescription(dto.getDescription());
                item.setType(dto.getType());
                item.setPrice(dto.getPrice());
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

        // ---------------- 8. Save performance ----------------
        Performance savedPerformance = performanceRepository.save(performance);

        // ---------------- 9. Assign creator as main ARTIST ----------------
        PerformanceUserRole creatorRole = new PerformanceUserRole();
        creatorRole.setPerformance(savedPerformance);
        creatorRole.setUser(requester);
        creatorRole.setRole(PerformanceRoleType.ARTIST);
        creatorRole.setMainArtist(true); // mark as main artist
        performanceUserRoleRepository.save(creatorRole);

        // ---------------- 10. Assign additional band members ----------------
        for (Long memberId : request.getBandMemberIds()) {
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new ApiException("Band member not found: " + memberId, HttpStatus.NOT_FOUND));

            // Skip creator if included in the list
            if (member.getId().equals(requester.getId())) {
                continue;
            }

            PerformanceUserRole role = new PerformanceUserRole();
            role.setPerformance(savedPerformance);
            role.setUser(member);
            role.setRole(PerformanceRoleType.ARTIST);
            role.setMainArtist(false);
            performanceUserRoleRepository.save(role);
        }

        // ---------------- 11. Build response ----------------
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedPerformance.getId());
        data.put("identifier", savedPerformance.getIdentifier());
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

    @Transactional
    public ApiResponse<Map<String, Object>> updatePerformance(PerformanceUpdateRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requester
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));

        // 3. Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        // 4. Verify requester is ARTIST of this performance
        boolean isArtist = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
                performance, requester, PerformanceRoleType.ARTIST);

        if (!isArtist) {
            throw new ApiException("Only ARTISTS of this performance can update it", HttpStatus.FORBIDDEN);
        }

        // 5. Apply updates (only if non-null)
        if (request.getName() != null && !request.getName().isBlank()) {
            // Ensure uniqueness within the festival
            boolean nameExists = performanceRepository.existsByFestivalAndName(
                    performance.getFestival(), request.getName());
            if (nameExists && !performance.getName().equals(request.getName())) {
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
            performance.setTechnicalRequirements(request.getTechnicalRequirements());
        }
        if (request.getSetlist() != null) {
            performance.setSetlist((Set<String>) request.getSetlist());
        }
        if (request.getMerchandiseItems() != null) {
            performance.setMerchandiseItems((Set<Performance.MerchandiseItem>) request.getMerchandiseItems());
        }
        if (request.getPreferredRehearsalTimes() != null) {
            performance.setPreferredRehearsalTimes((Set<String>) request.getPreferredRehearsalTimes());
        }
        if (request.getPreferredPerformanceSlots() != null) {
            performance.setPreferredPerformanceSlots((Set<String>) request.getPreferredPerformanceSlots());
        }

        // 6. Update band members if provided
        if (request.getBandMemberIds() != null) {
            List<User> bandMembers = userRepository.findAllById(request.getBandMemberIds());
            for (User member : bandMembers) {
                boolean alreadyMember = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
                        performance, member, PerformanceRoleType.BAND_MEMBER);
                if (!alreadyMember) {
                    PerformanceUserRole role = new PerformanceUserRole();
                    role.setPerformance(performance);
                    role.setUser(member);
                    role.setRole(PerformanceRoleType.BAND_MEMBER);
                    performanceUserRoleRepository.save(role);
                }
            }
        }

        Performance updated = performanceRepository.save(performance);

        // 7. Build response
        Map<String, Object> data = new HashMap<>();
        data.put("id", updated.getId());
        data.put("name", updated.getName());
        data.put("genre", updated.getGenre());
        data.put("state", updated.getState().name());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Performance updated successfully",
                data
        );
    }

    @Transactional
    public ApiResponse<Map<String, Object>> addBandMember(BandMemberAddRequest request) {
        // 1. Validate token
        Token token = tokenRepository.findByValue(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find requester
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.NOT_FOUND));

        // 3. Find performance
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new ApiException("Performance not found", HttpStatus.NOT_FOUND));

        // 4. Ensure requester is MAIN ARTIST (creator) of performance
        boolean isMainArtist = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
                performance, requester, PerformanceRoleType.ARTIST);

        if (!isMainArtist) {
            throw new ApiException("Only the main artist can add band members", HttpStatus.FORBIDDEN);
        }

        // 5. Find new band member
        User newMember = userRepository.findByUsername(request.getNewMemberUsername())
                .orElseThrow(() -> new ApiException("User to add not found", HttpStatus.NOT_FOUND));

        // 6. Ensure they are not already a band member
        boolean alreadyMember = performanceUserRoleRepository.existsByPerformanceAndUserAndRole(
                performance, newMember, PerformanceRoleType.BAND_MEMBER);

        if (alreadyMember) {
            throw new ApiException("This user is already a band member", HttpStatus.CONFLICT);
        }

        // 7. Add as BAND_MEMBER to performance
        PerformanceUserRole bandRole = new PerformanceUserRole();
        bandRole.setPerformance(performance);
        bandRole.setUser(newMember);
        bandRole.setRole(PerformanceRoleType.BAND_MEMBER);
        performanceUserRoleRepository.save(bandRole);

        // 8. Ensure the user is also ARTIST of the festival
        boolean isFestivalArtist = festivalUserRoleRepository.existsByFestivalAndUserAndRole(
                performance.getFestival(), newMember, FestivalRoleType.ARTIST);

        if (!isFestivalArtist) {
            FestivalUserRole festivalRole = new FestivalUserRole();
            festivalRole.setFestival(performance.getFestival());
            festivalRole.setUser(newMember);
            festivalRole.setRole(PerformanceRoleType.ARTIST);
            festivalUserRoleRepository.save(festivalRole);
        }

        // 9. Build response
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

}
