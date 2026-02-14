package com.ems.employeemanagementsystem.controller;

import com.ems.employeemanagementsystem.dto.ApiResponse;
import com.ems.employeemanagementsystem.dto.LeaveRequestDTO;
import com.ems.employeemanagementsystem.dto.LeaveStatusUpdateDTO;
import com.ems.employeemanagementsystem.service.LeaveRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<LeaveRequestDTO>> submitLeaveRequest(
            @Valid @RequestBody LeaveRequestDTO leaveRequestDTO) {
        logger.info("POST /api/leaves - Submitting leave request for employee ID: {}",
                leaveRequestDTO.getEmployeeId());
        LeaveRequestDTO createdRequest = leaveRequestService.submitLeaveRequest(leaveRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdRequest, "Leave request submitted successfully"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestDTO>> updateLeaveStatus(
            @PathVariable Long id,
            @Valid @RequestBody LeaveStatusUpdateDTO statusUpdate) {
        logger.info("PUT /api/leaves/{}/status - Updating status to: {}", id, statusUpdate.getStatus());
        LeaveRequestDTO updatedRequest = leaveRequestService.updateLeaveStatus(id, statusUpdate);
        return ResponseEntity.ok(ApiResponse.success(updatedRequest, "Leave status updated successfully"));
    }

    @GetMapping("/employee/{empId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<LeaveRequestDTO>>> getEmployeeLeaves(@PathVariable Long empId) {
        logger.info("GET /api/leaves/employee/{}", empId);
        List<LeaveRequestDTO> leaves = leaveRequestService.getEmployeeLeaves(empId);
        return ResponseEntity.ok(ApiResponse.success(leaves, "Employee leaves retrieved successfully"));
    }
}
