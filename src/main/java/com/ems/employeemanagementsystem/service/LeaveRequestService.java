package com.ems.employeemanagementsystem.service;

import com.ems.employeemanagementsystem.dto.LeaveRequestDTO;
import com.ems.employeemanagementsystem.dto.LeaveStatusUpdateDTO;
import com.ems.employeemanagementsystem.dto.NotificationDTO;
import com.ems.employeemanagementsystem.entity.Employee;
import com.ems.employeemanagementsystem.entity.LeaveRequest;
import com.ems.employeemanagementsystem.entity.enums.LeaveStatus;
import com.ems.employeemanagementsystem.exception.BadRequestException;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.messaging.NotificationPublisher;
import com.ems.employeemanagementsystem.repository.LeaveRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestService.class);

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeService employeeService;
    private final NotificationPublisher notificationPublisher;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                               EmployeeService employeeService,
                               NotificationPublisher notificationPublisher) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeService = employeeService;
        this.notificationPublisher = notificationPublisher;
    }

    public LeaveRequestDTO submitLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        logger.info("Submitting leave request for employee ID: {}", leaveRequestDTO.getEmployeeId());

        if (leaveRequestDTO.getEndDate().isBefore(leaveRequestDTO.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        Employee employee = employeeService.getEmployeeEntity(leaveRequestDTO.getEmployeeId());

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .startDate(leaveRequestDTO.getStartDate())
                .endDate(leaveRequestDTO.getEndDate())
                .reason(leaveRequestDTO.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request submitted successfully with ID: {}", savedRequest.getId());
        return mapToDTO(savedRequest);
    }

    public LeaveRequestDTO updateLeaveStatus(Long leaveId, LeaveStatusUpdateDTO statusUpdate) {
        logger.info("Updating leave request status. Leave ID: {}, New Status: {}",
                leaveId, statusUpdate.getStatus());

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request", "id", leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Can only update status of PENDING leave requests");
        }

        leaveRequest.setStatus(statusUpdate.getStatus());
        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request status updated successfully. Leave ID: {}", leaveId);

        try {
            Employee employee = leaveRequest.getEmployee();
            NotificationDTO notification = NotificationDTO.builder()
                    .employeeName(employee.getFullName())
                    .employeeEmail(employee.getEmail())
                    .leaveStartDate(leaveRequest.getStartDate().toString())
                    .leaveEndDate(leaveRequest.getEndDate().toString())
                    .leaveStatus(statusUpdate.getStatus().name())
                    .requestId(leaveId)
                    .build();
            notificationPublisher.publishLeaveStatusNotification(notification);
        } catch (Exception e) {
            logger.error("Failed to send leave status notification, but status was updated: {}",
                    e.getMessage());
        }

        return mapToDTO(updatedRequest);
    }

    public List<LeaveRequestDTO> getEmployeeLeaves(Long employeeId) {
        logger.info("Fetching leave requests for employee ID: {}", employeeId);
        employeeService.getEmployeeEntity(employeeId);

        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private LeaveRequestDTO mapToDTO(LeaveRequest leaveRequest) {
        return LeaveRequestDTO.builder()
                .id(leaveRequest.getId())
                .employeeId(leaveRequest.getEmployee().getId())
                .employeeName(leaveRequest.getEmployee().getFullName())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .status(leaveRequest.getStatus())
                .reason(leaveRequest.getReason())
                .createdAt(leaveRequest.getCreatedAt())
                .build();
    }
}
