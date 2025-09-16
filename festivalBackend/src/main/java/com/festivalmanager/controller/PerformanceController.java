package com.festivalmanager.controller;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.performance.PerformanceCreateRequest;
import com.festivalmanager.dto.performance.PerformanceUpdateRequest;
import com.festivalmanager.service.PerformanceService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

//    @Autowired
//    private PerformanceService performanceService;
//
//    /**
//     * Creates a new performance in a specific festival.
//     *
//     * @param request the performance creation request containing required
//     * information about the performance
//     * @return ApiResponse with operation status and performance info
//     */
//    @PostMapping("/create")
//    public ApiResponse<Map<String, Object>> createPerformance(@RequestBody PerformanceCreateRequest request) {
//        return performanceService.createPerformance(request);
//    }
//
//    @PutMapping("/update")
//    public ApiResponse<Map<String, Object>> updatePerformance(@RequestBody PerformanceUpdateRequest request) {
//        return performanceService.updatePerformance(request);
//    }

}
