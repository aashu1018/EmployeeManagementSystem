package com.ems.employeemanagementsystem.service;

import com.ems.employeemanagementsystem.dto.DepartmentDTO;
import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.entity.Department;
import com.ems.employeemanagementsystem.entity.Employee;
import com.ems.employeemanagementsystem.exception.DuplicateResourceException;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.repository.DepartmentRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;
    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .departmentName("Engineering")
                .location("Building A")
                .createdAt(LocalDateTime.now())
                .build();

        departmentDTO = DepartmentDTO.builder()
                .departmentName("Engineering")
                .location("Building A")
                .build();
    }

    @Test
    @DisplayName("Should return all departments")
    void getAllDepartments_ShouldReturnList() {
        Department dept2 = Department.builder()
                .id(2L)
                .departmentName("HR")
                .location("Building B")
                .build();

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(department, dept2));

        List<DepartmentDTO> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("Engineering");
        assertThat(result.get(1).getDepartmentName()).isEqualTo("HR");
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no departments")
    void getAllDepartments_ShouldReturnEmptyList() {
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        List<DepartmentDTO> result = departmentService.getAllDepartments();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return department by ID")
    void getDepartmentById_ShouldReturnDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        DepartmentDTO result = departmentService.getDepartmentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDepartmentName()).isEqualTo("Engineering");
    }

    @Test
    @DisplayName("Should throw exception when department not found")
    void getDepartmentById_ShouldThrowNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create department successfully")
    void createDepartment_ShouldCreateSuccessfully() {
        when(departmentRepository.existsByDepartmentName("Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentDTO result = departmentService.createDepartment(departmentDTO);

        assertThat(result.getDepartmentName()).isEqualTo("Engineering");
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("Should throw exception for duplicate department name")
    void createDepartment_ShouldThrowDuplicate() {
        when(departmentRepository.existsByDepartmentName("Engineering")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(departmentDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    @DisplayName("Should return employees of a department")
    void getDepartmentEmployees_ShouldReturnEmployees() {
        Employee employee = Employee.builder()
                .id(1L)
                .fullName("John")
                .email("john@test.com")
                .department(department)
                .salary(new BigDecimal("80000"))
                .joiningDate(LocalDate.now())
                .build();

        department.setEmployees(Collections.singletonList(employee));

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        List<EmployeeDTO> result = departmentService.getDepartmentEmployees(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should throw exception when getting employees for non-existent department")
    void getDepartmentEmployees_ShouldThrowNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentEmployees(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return department entity by ID")
    void getDepartmentEntity_ShouldReturnEntity() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Department result = departmentService.getDepartmentEntity(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }
}
