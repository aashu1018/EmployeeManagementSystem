package com.ems.employeemanagementsystem.controller;

import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.security.SecurityConfig;
import com.ems.employeemanagementsystem.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = EmployeeDTO.builder()
                .id(1L)
                .fullName("John Smith")
                .email("john@test.com")
                .departmentId(1L)
                .departmentName("Engineering")
                .salary(new BigDecimal("85000"))
                .joiningDate(LocalDate.of(2023, 1, 15))
                .build();
    }

    @Test
    @DisplayName("GET /api/employees - Should return paginated employees")
    @WithMockUser(roles = "ADMIN")
    void getAllEmployees_ShouldReturnPage() throws Exception {
        Page<EmployeeDTO> page = new PageImpl<>(Collections.singletonList(employeeDTO));
        when(employeeService.getAllEmployees(anyInt(), anyString(), anyString(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("sortBy", "name")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].fullName").value("John Smith"));
    }

    @Test
    @DisplayName("GET /api/employees - Should be accessible by USER role")
    @WithMockUser(roles = "USER")
    void getAllEmployees_ShouldBeAccessibleByUser() throws Exception {
        Page<EmployeeDTO> page = new PageImpl<>(Collections.singletonList(employeeDTO));
        when(employeeService.getAllEmployees(anyInt(), anyString(), anyString(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/employees - Should return 401 for unauthenticated")
    void getAllEmployees_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/employees/{id} - Should return employee")
    @WithMockUser(roles = "ADMIN")
    void getEmployeeById_ShouldReturnEmployee() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(employeeDTO);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Smith"))
                .andExpect(jsonPath("$.data.email").value("john@test.com"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} - Should return 404 for not found")
    @WithMockUser(roles = "ADMIN")
    void getEmployeeById_ShouldReturn404() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee", "id", 99L));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/employees - Should create employee (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void createEmployee_ShouldCreate() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Smith"));
    }

    @Test
    @DisplayName("POST /api/employees - Should return 403 for USER role")
    @WithMockUser(roles = "USER")
    void createEmployee_ShouldReturn403ForUser() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/employees - Should return 400 for invalid input")
    @WithMockUser(roles = "ADMIN")
    void createEmployee_ShouldReturn400ForInvalidInput() throws Exception {
        EmployeeDTO invalidDTO = EmployeeDTO.builder()
                .fullName("")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/employees/{id} - Should update employee (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_ShouldUpdate() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} - Should delete employee (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_ShouldDelete() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} - Should return 403 for USER role")
    @WithMockUser(roles = "USER")
    void deleteEmployee_ShouldReturn403ForUser() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isForbidden());
    }
}
