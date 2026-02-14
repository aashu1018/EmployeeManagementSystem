package com.ems.employeemanagementsystem.controller;

import com.ems.employeemanagementsystem.dto.ApiResponse;
import com.ems.employeemanagementsystem.dto.DepartmentDTO;
import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments() {
        logger.info("GET /api/departments");
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success(departments, "Departments retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        logger.info("POST /api/departments - Creating department: {}", departmentDTO.getDepartmentName());
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdDepartment, "Department created successfully"));
    }

    @GetMapping("/{id}/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getDepartmentEmployees(@PathVariable Long id) {
        logger.info("GET /api/departments/{}/employees", id);
        List<EmployeeDTO> employees = departmentService.getDepartmentEmployees(id);
        return ResponseEntity.ok(ApiResponse.success(employees, "Department employees retrieved successfully"));
    }
}
