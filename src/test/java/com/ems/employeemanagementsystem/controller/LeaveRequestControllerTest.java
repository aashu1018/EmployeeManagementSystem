package com.ems.employeemanagementsystem.controller;

import com.ems.employeemanagementsystem.dto.LeaveRequestDTO;
import com.ems.employeemanagementsystem.dto.LeaveStatusUpdateDTO;
import com.ems.employeemanagementsystem.entity.enums.LeaveStatus;
import com.ems.employeemanagementsystem.security.SecurityConfig;
import com.ems.employeemanagementsystem.service.LeaveRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaveRequestController.class)
@Import(SecurityConfig.class)
class LeaveRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaveRequestService leaveRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private LeaveRequestDTO leaveRequestDTO;

    @BeforeEach
    void setUp() {
        leaveRequestDTO = LeaveRequestDTO.builder()
                .id(1L)
                .employeeId(1L)
                .employeeName("John Smith")
                .startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20))
                .status(LeaveStatus.PENDING)
                .reason("Vacation")
                .build();
    }

    @Test
    @DisplayName("POST /api/leaves - Should submit leave request")
    @WithMockUser(roles = "USER")
    void submitLeaveRequest_ShouldSubmit() throws Exception {
        when(leaveRequestService.submitLeaveRequest(any(LeaveRequestDTO.class)))
                .thenReturn(leaveRequestDTO);

        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/leaves - Should be accessible by ADMIN")
    @WithMockUser(roles = "ADMIN")
    void submitLeaveRequest_ShouldBeAccessibleByAdmin() throws Exception {
        when(leaveRequestService.submitLeaveRequest(any(LeaveRequestDTO.class)))
                .thenReturn(leaveRequestDTO);

        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/leaves - Should return 401 for unauthenticated")
    void submitLeaveRequest_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/leaves/{id}/status - Should update status (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void updateLeaveStatus_ShouldUpdate() throws Exception {
        LeaveStatusUpdateDTO statusUpdate = new LeaveStatusUpdateDTO(LeaveStatus.APPROVED);
        LeaveRequestDTO approvedDTO = LeaveRequestDTO.builder()
                .id(1L)
                .employeeId(1L)
                .employeeName("John Smith")
                .startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20))
                .status(LeaveStatus.APPROVED)
                .reason("Vacation")
                .build();

        when(leaveRequestService.updateLeaveStatus(eq(1L), any(LeaveStatusUpdateDTO.class)))
                .thenReturn(approvedDTO);

        mockMvc.perform(put("/api/leaves/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    @DisplayName("PUT /api/leaves/{id}/status - Should return 403 for USER")
    @WithMockUser(roles = "USER")
    void updateLeaveStatus_ShouldReturn403ForUser() throws Exception {
        LeaveStatusUpdateDTO statusUpdate = new LeaveStatusUpdateDTO(LeaveStatus.APPROVED);

        mockMvc.perform(put("/api/leaves/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/leaves/employee/{empId} - Should return employee leaves")
    @WithMockUser(roles = "USER")
    void getEmployeeLeaves_ShouldReturnLeaves() throws Exception {
        when(leaveRequestService.getEmployeeLeaves(1L))
                .thenReturn(Collections.singletonList(leaveRequestDTO));

        mockMvc.perform(get("/api/leaves/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/leaves/employee/{empId} - Should be accessible by ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getEmployeeLeaves_ShouldBeAccessibleByAdmin() throws Exception {
        when(leaveRequestService.getEmployeeLeaves(1L))
                .thenReturn(Collections.singletonList(leaveRequestDTO));

        mockMvc.perform(get("/api/leaves/employee/1"))
                .andExpect(status().isOk());
    }
}
