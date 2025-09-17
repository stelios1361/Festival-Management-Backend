package com.festivalmanager.controller;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.performance.BandMemberAddRequest;
import com.festivalmanager.dto.performance.PerformanceAcceptanceRequest;
import com.festivalmanager.dto.performance.PerformanceAssignStaffRequest;
import com.festivalmanager.dto.performance.PerformanceCreateRequest;
import com.festivalmanager.dto.performance.PerformanceFinalSubmissionRequest;
import com.festivalmanager.dto.performance.PerformanceRejectionRequest;
import com.festivalmanager.dto.performance.PerformanceReviewRequest;
import com.festivalmanager.dto.performance.PerformanceSubmitRequest;
import com.festivalmanager.dto.performance.PerformanceUpdateRequest;
import com.festivalmanager.dto.performance.PerformanceWithdrawRequest;
import com.festivalmanager.service.PerformanceService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    /**
     * Creates a new performance in a specific festival.
     *
     * @param request the performance creation request containing required
     * information about the performance
     * @return ApiResponse with operation status and performance info
     */
    @PostMapping("/createperformance")
    public ApiResponse<Map<String, Object>> createPerformance(@RequestBody PerformanceCreateRequest request) {
        return performanceService.createPerformance(request);
    }

    @PutMapping("/updateperformance")
    public ApiResponse<Map<String, Object>> updatePerformance(@RequestBody PerformanceUpdateRequest request) {
        return performanceService.updatePerformance(request);
    }

    @PostMapping("/addbandmember")
    public ApiResponse<Map<String, Object>> addBandMembers(@RequestBody BandMemberAddRequest request) {
        return performanceService.addBandMember(request);
    }

    @PostMapping("/submitperformance")
    public ApiResponse<Map<String, Object>> submitPerformance(@RequestBody PerformanceSubmitRequest request) {
        return performanceService.submitPerformance(request);
    }

    @PostMapping("/withdrawperformance")
    public ApiResponse<Map<String, Object>> withdrawPerformance(@RequestBody PerformanceWithdrawRequest request) {
        return performanceService.withdrawPerformance(request);
    }

    @PostMapping("/assignstaffperformance")
    public ApiResponse<Map<String, Object>> assignStaffToPerformance(@RequestBody PerformanceAssignStaffRequest request) {
        return performanceService.assignStaffToPerformance(request);
    }

    @PostMapping("/reviewperformance")
    public ApiResponse<Map<String, Object>> reviewPerformance(@RequestBody PerformanceReviewRequest request) {
        return performanceService.reviewPerformance(request);
    }

    @PostMapping("/manualrejextperformance")
    public ApiResponse<Map<String, Object>> rejectPerformanceManually(@RequestBody PerformanceRejectionRequest request) {
        return performanceService.rejectPerformanceManually(request);
    }

    @PostMapping("/submitfinalperformance")
    public ApiResponse<Map<String, Object>> submitFinalPerformance(@RequestBody PerformanceFinalSubmissionRequest request) {
        return performanceService.submitFinalPerformance(request);
    }

    @PostMapping("/accpetperformance")
    public ApiResponse<Map<String, Object>> accpetPerformance(@RequestBody PerformanceAcceptanceRequest request) {
        return performanceService.accpetPerformance(request);
    }

}
