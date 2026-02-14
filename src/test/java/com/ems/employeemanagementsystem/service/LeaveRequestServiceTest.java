package com.ems.employeemanagementsystem.service;

import com.ems.employeemanagementsystem.dto.LeaveRequestDTO;
import com.ems.employeemanagementsystem.dto.LeaveStatusUpdateDTO;
import com.ems.employeemanagementsystem.entity.Department;
import com.ems.employeemanagementsystem.entity.Employee;
import com.ems.employeemanagementsystem.entity.LeaveRequest;
import com.ems.employeemanagementsystem.entity.enums.LeaveStatus;
import com.ems.employeemanagementsystem.exception.BadRequestException;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.messaging.NotificationPublisher;
import com.ems.employeemanagementsystem.repository.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    private Department department;
    private Employee employee;
    private LeaveRequest leaveRequest;
    private LeaveRequestDTO leaveRequestDTO;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .departmentName("Engineering")
                .build();

        employee = Employee.builder()
                .id(1L)
                .fullName("John Smith")
                .email("john@test.com")
                .department(department)
                .salary(new BigDecimal("85000"))
                .build();

        leaveRequest = LeaveRequest.builder()
                .id(1L)
                .employee(employee)
                .startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20))
                .status(LeaveStatus.PENDING)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();

        leaveRequestDTO = LeaveRequestDTO.builder()
                .employeeId(1L)
                .startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20))
                .reason("Vacation")
                .build();
    }

    @Test
    @DisplayName("Should submit leave request successfully")
    void submitLeaveRequest_ShouldSubmitSuccessfully() {
        when(employeeService.getEmployeeEntity(1L)).thenReturn(employee);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequestDTO result = leaveRequestService.submitLeaveRequest(leaveRequestDTO);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(LeaveStatus.PENDING);
        assertThat(result.getEmployeeName()).isEqualTo("John Smith");
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when end date is before start date")
    void submitLeaveRequest_ShouldThrowForInvalidDates() {
        LeaveRequestDTO invalidDTO = LeaveRequestDTO.builder()
                .employeeId(1L)
                .startDate(LocalDate.of(2024, 3, 20))
                .endDate(LocalDate.of(2024, 3, 15))
                .reason("Vacation")
                .build();

        assertThatThrownBy(() -> leaveRequestService.submitLeaveRequest(invalidDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("End date must be after start date");
    }

    @Test
    @DisplayName("Should update leave status to APPROVED")
    void updateLeaveStatus_ShouldApprove() {
        LeaveStatusUpdateDTO statusUpdate = new LeaveStatusUpdateDTO(LeaveStatus.APPROVED);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        LeaveRequest approvedRequest = LeaveRequest.builder()
                .id(1L)
                .employee(employee)
                .startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20))
                .status(LeaveStatus.APPROVED)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(approvedRequest);

        LeaveRequestDTO result = leaveRequestService.updateLeaveStatus(1L, statusUpdate);

        assertThat(result.getStatus()).isEqualTo(LeaveStatus.APPROVED);
        verify(notificationPublisher, times(1)).publishLeaveStatusNotification(any());
    }

    @Test
    @DisplayName("Should update leave status to REJECTED")
    void updateLeaveStatus_ShouldReject() {
        LeaveStatusUpdateDTO statusUpdate = new LeaveStatusUpdateDTO(LeaveStatus.REJECTED);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        LeaveRequest rejectedRequest = LeaveRequest.builder()
                .id(1L)
                .employee(employee)
                .startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20))
                .status(LeaveStatus.REJECTED)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(rejectedRequest);

        LeaveRequestDTO result = leaveRequestService.updateLeaveStatus(1L, statusUpdate);

        assertThat(result.getStatus()).isEqualTo(LeaveStatus.REJECTED);
    }

    @Test
    @DisplayName("Should throw exception when leave not found for status update")
    void updateLeaveStatus_ShouldThrowNotFound() {
        when(leaveRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestService.updateLeaveStatus(99L,
                new LeaveStatusUpdateDTO(LeaveStatus.APPROVED)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception when updating non-PENDING leave")
    void updateLeaveStatus_ShouldThrowForNonPending() {
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThatThrownBy(() -> leaveRequestService.updateLeaveStatus(1L,
                new LeaveStatusUpdateDTO(LeaveStatus.REJECTED)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("PENDING");
    }

    @Test
    @DisplayName("Should return employee leaves")
    void getEmployeeLeaves_ShouldReturnLeaves() {
        LeaveRequest leave2 = LeaveRequest.builder()
                .id(2L)
                .employee(employee)
                .startDate(LocalDate.of(2024, 5, 1))
                .endDate(LocalDate.of(2024, 5, 3))
                .status(LeaveStatus.PENDING)
                .reason("Personal")
                .createdAt(LocalDateTime.now())
                .build();

        when(employeeService.getEmployeeEntity(1L)).thenReturn(employee);
        when(leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(leaveRequest, leave2));

        List<LeaveRequestDTO> result = leaveRequestService.getEmployeeLeaves(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should throw exception when getting leaves for non-existent employee")
    void getEmployeeLeaves_ShouldThrowNotFound() {
        when(employeeService.getEmployeeEntity(99L))
                .thenThrow(new ResourceNotFoundException("Employee", "id", 99L));

        assertThatThrownBy(() -> leaveRequestService.getEmployeeLeaves(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should handle notification failure gracefully during status update")
    void updateLeaveStatus_ShouldHandleNotificationFailure() {
        LeaveStatusUpdateDTO statusUpdate = new LeaveStatusUpdateDTO(LeaveStatus.APPROVED);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        LeaveRequest approvedRequest = LeaveRequest.builder()
                .id(1L)
                .employee(employee)
                .startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20))
                .status(LeaveStatus.APPROVED)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(approvedRequest);
        doThrow(new RuntimeException("RabbitMQ down")).when(notificationPublisher)
                .publishLeaveStatusNotification(any());

        LeaveRequestDTO result = leaveRequestService.updateLeaveStatus(1L, statusUpdate);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LeaveStatus.APPROVED);
    }
}
