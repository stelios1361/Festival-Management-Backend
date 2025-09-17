package com.festivalmanager.controller;

import com.festivalmanager.dto.festival.*;
import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.service.FestivalService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing festival-related operations. This controller
 * provides endpoints to create, update, delete, and manage festivals as well as
 * their submissions, staff, organizers, scheduling, reviews, decisions, and
 * announcements.
 *
 * <p>
 * All methods return a standardized {@link ApiResponse} containing the result
 */
@RestController
@RequestMapping("/api/festivals")
public class FestivalController {

    @Autowired
    private FestivalService festivalService;

    /**
     * Creates a new festival.
     *
     * @param request the festival creation request containing all necessary
     * information to create a festival
     * @return an ApiResponse containing the details of the created festival
     * @throws ApiException if the creation process fails
     */
    @PostMapping("/createfestival")
    public ApiResponse<Map<String, Object>> createFestival(@RequestBody FestivalCreateRequest request) throws ApiException {
        return festivalService.createFestival(request);
    }

    /**
     * Updates an existing festival.
     *
     * @param request the festival update request containing the updated
     * festival information
     * @return an ApiResponse containing the updated festival details
     * @throws ApiException if the update process fails
     */
    @PutMapping("/updatefestival")
    public ApiResponse<Map<String, Object>> updateFestival(@RequestBody FestivalUpdateRequest request) throws ApiException {
        return festivalService.updateFestival(request);
    }

    /**
     * Adds organizers to a festival.
     *
     * @param request the request containing the information needed
     * @return an ApiResponse with operation status
     * @throws ApiException if adding organizers fails
     */
    @PostMapping("/addorganizers")
    public ApiResponse<Map<String, Object>> addOrganizers(@RequestBody AddOrganizersRequest request) throws ApiException {
        return festivalService.addOrganizers(request);
    }

    /**
     * Adds staff members to a festival.
     *
     * @param request the request containing the information needed
     * @return an ApiResponse with operation status
     * @throws ApiException if adding staff fails
     */
    @PostMapping("/addstaff")
    public ApiResponse<Map<String, Object>> addStaff(@RequestBody AddStaffRequest request) throws ApiException {
        return festivalService.addStaff(request);
    }

    /**
     * Searches for festivals based on search criteria.
     *
     * @param request the festival search request parameters
     * @return an ApiResponse containing the list of matching festivals
     * @throws ApiException if the search process fails
     */
    @GetMapping("/searchfestivals")
    public ApiResponse<Map<String, Object>> searchfestivals(@RequestBody FestivalSearchRequest request) throws ApiException {
        return festivalService.searchFestivals(request);
    }

    /**
     * Views the details of a specific festival.
     *
     * @param request the festival view request
     * @return an ApiResponse containing the festival details
     * @throws ApiException if retrieving festival details fails
     */
    @GetMapping("/viewfestival")
    public ApiResponse<Map<String, Object>> viewfestival(@RequestBody FestivalViewRequest request) throws ApiException {
        return festivalService.viewFestival(request);
    }

    /**
     * Deletes a festival.
     *
     * @param request the festival delete request
     * @return an ApiResponse with operation status
     * @throws ApiException if deletion fails
     */
    @DeleteMapping("/deletefestival")
    public ApiResponse<Map<String, Object>> deleteFestival(@RequestBody FestivalDeleteRequest request) throws ApiException {
        return festivalService.deleteFestival(request);
    }

    /**
     * Starts the submission phase for a festival.
     *
     * @param request the submission start request
     * @return an ApiResponse with operation status
     * @throws ApiException if starting submission fails
     */
    @PostMapping("/startsubmission")
    public ApiResponse<Map<String, Object>> startSubmission(@RequestBody SubmissionStartRequest request) throws ApiException {
        return festivalService.startSubmission(request);
    }

    /**
     * Starts the stage manager assignment phase for a festival.
     *
     * @param request the request containing the information needed
     * @return an ApiResponse with operation status
     * @throws ApiException if starting stage manager assignment fails
     */
    @PostMapping("/startstagemanagement")
    public ApiResponse<Map<String, Object>> startStageManagerAssignment(@RequestBody StageManagerAssignmentStartRequest request) throws ApiException {
        return festivalService.startStageManagerAssignment(request);
    }

    /**
     * Starts the review process for a festival.
     *
     * @param request the review start request
     * @return an ApiResponse with operation status
     * @throws ApiException if starting review fails
     */
    @PostMapping("/startreview")
    public ApiResponse<Map<String, Object>> startReview(@RequestBody ReviewStartRequest request) throws ApiException {
        return festivalService.startReview(request);
    }

    /**
     * Starts the scheduling phase for a festival.
     *
     * @param request the schedule making request
     * @return an ApiResponse with operation status
     * @throws ApiException if starting schedule making fails
     */
    @PostMapping("/startscheduling")
    public ApiResponse<Map<String, Object>> startScheduleMaking(@RequestBody ScheduleMakingRequest request) throws ApiException {
        return festivalService.startScheduleMaking(request);
    }

    /**
     * Starts the final submission phase for a festival.
     *
     * @param request the final submission start request
     * @return an ApiResponse with operation status
     * @throws ApiException if starting final submission fails
     */
    @PostMapping("/startfinalsubmission")
    public ApiResponse<Map<String, Object>> startFinalSubmission(@RequestBody FinalSubmissionStartRequest request) throws ApiException {
        return festivalService.startFinalSubmission(request);
    }

    /**
     * Starts the decision-making process for a festival.
     *
     * @param request the decision-making request
     * @return an ApiResponse with operation status
     * @throws ApiException if decision making fails
     */
    @PostMapping("/makedecision")
    public ApiResponse<Map<String, Object>> makeDecision(@RequestBody DecisionMakingRequest request) throws ApiException {
        return festivalService.startDecisionMaking(request);
    }

    /**
     * Announces a festival publicly.
     *
     * @param request the festival announcement request
     * @return an ApiResponse with operation status
     * @throws ApiException if announcing the festival fails
     */
    @PostMapping("/announcefestival")
    public ApiResponse<Map<String, Object>> announceFestival(@RequestBody FestivalAnnouncementRequest request) throws ApiException {
        return festivalService.announceFestival(request);
    }

}
