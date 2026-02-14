package com.ems.employeemanagementsystem.controller;

import com.ems.employeemanagementsystem.dto.ApiResponse;
import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Page<EmployeeDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long departmentId) {

        logger.info("GET /api/employees - page: {}, sortBy: {}, sortDir: {}, departmentId: {}",
                page, sortBy, sortDir, departmentId);

        Page<EmployeeDTO> employees = employeeService.getAllEmployees(page, sortBy, sortDir, departmentId);
        return ResponseEntity.ok(ApiResponse.success(employees, "Employees retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long id) {
        logger.info("GET /api/employees/{}", id);
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(employee, "Employee retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        logger.info("POST /api/employees - Creating employee: {}", employeeDTO.getFullName());
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdEmployee, "Employee created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        logger.info("PUT /api/employees/{} - Updating employee", id);
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedEmployee, "Employee updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        logger.info("DELETE /api/employees/{}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Employee deleted successfully"));
    }
}
