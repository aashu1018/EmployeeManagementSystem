package com.ems.employeemanagementsystem.service;

import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.entity.Department;
import com.ems.employeemanagementsystem.entity.Employee;
import com.ems.employeemanagementsystem.exception.DuplicateResourceException;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.messaging.NotificationPublisher;
import com.ems.employeemanagementsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private EmployeeService employeeService;

    private Department department;
    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .departmentName("Engineering")
                .location("Building A")
                .build();

        employee = Employee.builder()
                .id(1L)
                .fullName("John Smith")
                .email("john@test.com")
                .department(department)
                .salary(new BigDecimal("85000"))
                .joiningDate(LocalDate.of(2023, 1, 15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        employeeDTO = EmployeeDTO.builder()
                .fullName("John Smith")
                .email("john@test.com")
                .departmentId(1L)
                .salary(new BigDecimal("85000"))
                .joiningDate(LocalDate.of(2023, 1, 15))
                .build();
    }

    @Test
    @DisplayName("Should return paginated employees")
    void getAllEmployees_ShouldReturnPage() {
        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(employee));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);

        Page<EmployeeDTO> result = employeeService.getAllEmployees(0, "name", "asc", null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("John Smith");
    }

    @Test
    @DisplayName("Should return employees filtered by department")
    void getAllEmployees_WithDepartmentFilter_ShouldReturnFiltered() {
        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(employee));
        when(employeeRepository.findByDepartmentId(eq(1L), any(Pageable.class))).thenReturn(employeePage);

        Page<EmployeeDTO> result = employeeService.getAllEmployees(0, "name", "asc", 1L);

        assertThat(result.getContent()).hasSize(1);
        verify(employeeRepository).findByDepartmentId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return employees sorted descending")
    void getAllEmployees_WithDescSort_ShouldReturnSorted() {
        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(employee));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);

        Page<EmployeeDTO> result = employeeService.getAllEmployees(0, "name", "desc", null);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should return employee by ID")
    void getEmployeeById_ShouldReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeService.getEmployeeById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("John Smith");
        assertThat(result.getDepartmentName()).isEqualTo("Engineering");
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void getEmployeeById_ShouldThrowNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create employee successfully")
    void createEmployee_ShouldCreateSuccessfully() {
        when(employeeRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(departmentService.getDepartmentEntity(1L)).thenReturn(department);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertThat(result.getFullName()).isEqualTo("John Smith");
        assertThat(result.getEmail()).isEqualTo("john@test.com");
        verify(notificationPublisher, times(1)).publishEmployeeNotification(any());
    }

    @Test
    @DisplayName("Should throw exception for duplicate email on create")
    void createEmployee_ShouldThrowDuplicateEmail() {
        when(employeeRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(employeeDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should update employee successfully")
    void updateEmployee_ShouldUpdateSuccessfully() {
        EmployeeDTO updateDTO = EmployeeDTO.builder()
                .fullName("John Updated")
                .email("john@test.com")
                .departmentId(1L)
                .salary(new BigDecimal("90000"))
                .joiningDate(LocalDate.of(2023, 1, 15))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentService.getDepartmentEntity(1L)).thenReturn(department);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO result = employeeService.updateEmployee(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception for duplicate email on update")
    void updateEmployee_ShouldThrowDuplicateEmail() {
        EmployeeDTO updateDTO = EmployeeDTO.builder()
                .fullName("John Updated")
                .email("different@test.com")
                .departmentId(1L)
                .salary(new BigDecimal("90000"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail("different@test.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.updateEmployee(1L, updateDTO))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent employee")
    void updateEmployee_ShouldThrowNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(99L, employeeDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete employee successfully")
    void deleteEmployee_ShouldDeleteSuccessfully() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent employee")
    void deleteEmployee_ShouldThrowNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return employee entity by ID")
    void getEmployeeEntity_ShouldReturnEntity() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeEntity(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle notification failure gracefully during create")
    void createEmployee_ShouldHandleNotificationFailure() {
        when(employeeRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(departmentService.getDepartmentEntity(1L)).thenReturn(department);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        doThrow(new RuntimeException("RabbitMQ down")).when(notificationPublisher).publishEmployeeNotification(any());

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("John Smith");
    }
}
