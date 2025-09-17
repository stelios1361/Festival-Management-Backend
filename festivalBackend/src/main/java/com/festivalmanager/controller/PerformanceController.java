package com.festivalmanager.controller;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.performance.*;
import com.festivalmanager.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing performance-related operations, including
 * creation, updating, submission, withdrawal, review, approval, rejection, and
 * viewing of performances.
 */
@RestController
@RequestMapping("/api/performances")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    /**
     * Creates a new performance in a festival.
     *
     * @param request performance creation request containing performance
     * details
     * @return ApiResponse with operation status and created performance info
     */
    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> createPerformance(@RequestBody PerformanceCreateRequest request) {
        return performanceService.createPerformance(request);
    }

    /**
     * Updates an existing performance.
     *
     * @param request performance update request containing updated performance
     * info
     * @return ApiResponse with operation status and updated performance info
     */
    @PutMapping("/update")
    public ApiResponse<Map<String, Object>> updatePerformance(@RequestBody PerformanceUpdateRequest request) {
        return performanceService.updatePerformance(request);
    }

    /**
     * Adds a band member to a performance.
     *
     * @param request band member addition request containing performance ID and
     * member info
     * @return ApiResponse with operation status
     */
    @PostMapping("/addbandmember")
    public ApiResponse<Map<String, Object>> addBandMember(@RequestBody BandMemberAddRequest request) {
        return performanceService.addBandMember(request);
    }

    /**
     * Submits a performance for review or approval.
     *
     * @param request performance submission request
     * @return ApiResponse with operation status
     */
    @PostMapping("/submit")
    public ApiResponse<Map<String, Object>> submitPerformance(@RequestBody PerformanceSubmitRequest request) {
        return performanceService.submitPerformance(request);
    }

    /**
     * Withdraws a performance from the festival.
     *
     * @param request performance withdrawal request
     * @return ApiResponse with operation status
     */
    @PostMapping("/withdraw")
    public ApiResponse<Map<String, Object>> withdrawPerformance(@RequestBody PerformanceWithdrawRequest request) {
        return performanceService.withdrawPerformance(request);
    }

    /**
     * Assigns a staff member to manage a performance.
     *
     * @param request staff assignment request containing performance ID and
     * staff info
     * @return ApiResponse with operation status
     */
    @PostMapping("/assignstaff")
    public ApiResponse<Map<String, Object>> assignStaff(@RequestBody PerformanceAssignStaffRequest request) {
        return performanceService.assignStaffToPerformance(request);
    }

    /**
     * Approves a performance (festival organizer action).
     *
     * @param request performance approval request
     * @return ApiResponse with operation status
     */
    @PostMapping("/approve")
    public ApiResponse<Map<String, Object>> approvePerformance(@RequestBody PerformanceApprovalRequest request) {
        return performanceService.approvePerformance(request);
    }

    /**
     * Reviews a performance and adds score or comments (staff action).
     *
     * @param request performance review request
     * @return ApiResponse with operation status
     */
    @PostMapping("/review")
    public ApiResponse<Map<String, Object>> reviewPerformance(@RequestBody PerformanceReviewRequest request) {
        return performanceService.reviewPerformance(request);
    }

    /**
     * Manually rejects a performance with a reason (organizer action).
     *
     * @param request performance rejection request
     * @return ApiResponse with operation status
     */
    @PostMapping("/reject")
    public ApiResponse<Map<String, Object>> rejectPerformance(@RequestBody PerformanceRejectionRequest request) {
        return performanceService.rejectPerformanceManually(request);
    }

    /**
     * Submits final performance details (artist action).
     *
     * @param request final performance submission request
     * @return ApiResponse with operation status
     */
    @PostMapping("/submitfinal")
    public ApiResponse<Map<String, Object>> submitFinalPerformance(@RequestBody PerformanceFinalSubmissionRequest request) {
        return performanceService.submitFinalPerformance(request);
    }

    /**
     * Accepts a performance after review (organizer action).
     *
     * @param request performance acceptance request
     * @return ApiResponse with operation status
     */
    @PostMapping("/accept")
    public ApiResponse<Map<String, Object>> acceptPerformance(@RequestBody PerformanceAcceptanceRequest request) {
        return performanceService.acceptPerformance(request);
    }

    /**
     * Views performance details. Level of detail depends on requester role.
     *
     * @param request performance view request
     * @return ApiResponse with performance details
     */
    @GetMapping("/view")
    public ApiResponse<Map<String, Object>> viewPerformance(@RequestBody PerformanceViewRequest request) {
        return performanceService.viewPerformance(request);
    }

    /**
     * Searches performances with optional filters (name, artist, genre).
     *
     * @param request performance search request
     * @return ApiResponse with list of matching performances
     */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchPerformances(@RequestBody PerformanceSearchRequest request) {
        return performanceService.searchPerformances(request);
    }
}
