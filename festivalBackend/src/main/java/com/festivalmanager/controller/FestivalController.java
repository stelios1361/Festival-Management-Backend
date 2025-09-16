package com.festivalmanager.controller;

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
import com.festivalmanager.dto.festival.FestivalSearchRequest;
import com.festivalmanager.dto.festival.FestivalViewRequest;
import com.festivalmanager.service.FestivalService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing festival-related operations, including creation,
 * add more........
 */
@RestController
@RequestMapping("/api/festivals")
public class FestivalController {

    @Autowired
    private FestivalService festivalService;

    /**
     * Creates a new festival in the system.
     *
     * @param request festival creation request containing required data about
     * the festival
     *
     * @return ApiResponse with operation status
     */
    @PostMapping("/createfestival")
    public ApiResponse<Map<String, Object>> createFestival(@RequestBody FestivalCreateRequest request) {
        return festivalService.createFestival(request);
    }

    /**
     * Updates an existing festival in the system.
     *
     * @param request festival update request containing updated data
     *
     * @return ApiResponse with operation status
     */
    @PutMapping("/updatefestival")
    public ApiResponse<Map<String, Object>> updateFestival(@RequestBody FestivalUpdateRequest request) {
        return festivalService.updateFestival(request);
    }

    @PostMapping("/addorganizers")
    public ApiResponse<Map<String, Object>> addOrganizers(@RequestBody AddOrganizersRequest request) {
        return festivalService.addOrganizers(request);
    }

    @PostMapping("/addstaff")
    public ApiResponse<Map<String, Object>> addStaff(@RequestBody AddStaffRequest request) {
        return festivalService.addStaff(request);
    }

    @GetMapping("/searchfestivals")
    public ApiResponse<Map<String, Object>> searchfestivals(@RequestBody FestivalSearchRequest request) {
        return festivalService.searchFestivals(request);
    }

    @GetMapping("/viewfestival")
    public ApiResponse<Map<String, Object>> viewfestival(@RequestBody FestivalViewRequest request) {
        return festivalService.viewFestival(request);
    }

    @DeleteMapping("/deletefestival")
    public ApiResponse<Map<String, Object>> deleteFestival(@RequestBody FestivalDeleteRequest request) {
        return festivalService.deleteFestival(request);
    }

    @PostMapping("/startsubmission")
    public ApiResponse<Map<String, Object>> startSubmission(@RequestBody SubmissionStartRequest request) {
        return festivalService.startSubmission(request);
    }

    @PostMapping("/startstagemanagement")
    public ApiResponse<Map<String, Object>> startStageManagerAssignment(@RequestBody StageManagerAssignmentStartRequest request) {
        return festivalService.startStageManagerAssignment(request);
    }

    @PostMapping("/startreview")
    public ApiResponse<Map<String, Object>> startReview(@RequestBody ReviewStartRequest request) {
        return festivalService.startReview(request);
    }

    @PostMapping("/startscheduling")
    public ApiResponse<Map<String, Object>> startScheduleMaking(@RequestBody ScheduleMakingRequest request) {
        return festivalService.startScheduleMaking(request);
    }

    @PostMapping("/startfinalsubmission")
    public ApiResponse<Map<String, Object>> startFinalSubmission(@RequestBody FinalSubmissionStartRequest request) {
        return festivalService.startFinalSubmission(request);
    }

    @PostMapping("/makedecision")
    public ApiResponse<Map<String, Object>> makeDecision(@RequestBody DecisionMakingRequest request) {
        return festivalService.startDecisionMaking(request);
    }

    @PostMapping("/announcefestival")
    public ApiResponse<Map<String, Object>> announceFestival(@RequestBody FestivalAnnouncementRequest request) {
        return festivalService.announceFestival(request);
    }

}
