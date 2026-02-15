package com.ems.employeemanagementsystem.controller;

import com.ems.employeemanagementsystem.dto.DepartmentDTO;
import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.security.SecurityConfig;
import com.ems.employeemanagementsystem.service.DepartmentService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@Import(SecurityConfig.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        departmentDTO = DepartmentDTO.builder()
                .id(1L)
                .departmentName("Engineering")
                .location("Building A")
                .build();
    }

    @Test
    @DisplayName("GET /api/departments - Should return all departments")
    @WithMockUser(roles = "ADMIN")
    void getAllDepartments_ShouldReturnList() throws Exception {
        DepartmentDTO dept2 = DepartmentDTO.builder()
                .id(2L)
                .departmentName("HR")
                .location("Building B")
                .build();

        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(departmentDTO, dept2));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/departments - Should be accessible by USER role")
    @WithMockUser(roles = "USER")
    void getAllDepartments_ShouldBeAccessibleByUser() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(Collections.singletonList(departmentDTO));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/departments - Should create department (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void createDepartment_ShouldCreate() throws Exception {
        when(departmentService.createDepartment(any(DepartmentDTO.class))).thenReturn(departmentDTO);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.departmentName").value("Engineering"));
    }

    @Test
    @DisplayName("POST /api/departments - Should return 403 for USER role")
    @WithMockUser(roles = "USER")
    void createDepartment_ShouldReturn403ForUser() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/departments - Should return 400 for invalid input")
    @WithMockUser(roles = "ADMIN")
    void createDepartment_ShouldReturn400ForInvalidInput() throws Exception {
        DepartmentDTO invalidDTO = DepartmentDTO.builder()
                .departmentName("")
                .build();

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/departments/{id}/employees - Should return employees")
    @WithMockUser(roles = "ADMIN")
    void getDepartmentEmployees_ShouldReturnEmployees() throws Exception {
        EmployeeDTO emp = EmployeeDTO.builder()
                .id(1L)
                .fullName("John")
                .email("john@test.com")
                .departmentId(1L)
                .departmentName("Engineering")
                .salary(new BigDecimal("80000"))
                .joiningDate(LocalDate.now())
                .build();

        when(departmentService.getDepartmentEmployees(1L)).thenReturn(Collections.singletonList(emp));

        mockMvc.perform(get("/api/departments/1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].fullName").value("John"));
    }

    @Test
    @DisplayName("GET /api/departments/{id}/employees - Should return 404")
    @WithMockUser(roles = "ADMIN")
    void getDepartmentEmployees_ShouldReturn404() throws Exception {
        when(departmentService.getDepartmentEmployees(99L))
                .thenThrow(new ResourceNotFoundException("Department", "id", 99L));

        mockMvc.perform(get("/api/departments/99/employees"))
                .andExpect(status().isNotFound());
    }
}
