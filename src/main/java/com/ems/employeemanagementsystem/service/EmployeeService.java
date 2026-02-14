package com.ems.employeemanagementsystem.service;

import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.dto.NotificationDTO;
import com.ems.employeemanagementsystem.entity.Department;
import com.ems.employeemanagementsystem.entity.Employee;
import com.ems.employeemanagementsystem.exception.DuplicateResourceException;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.messaging.NotificationPublisher;
import com.ems.employeemanagementsystem.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final NotificationPublisher notificationPublisher;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentService departmentService,
                           NotificationPublisher notificationPublisher) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.notificationPublisher = notificationPublisher;
    }

    public Page<EmployeeDTO> getAllEmployees(int page, String sortBy, String sortDir, Long departmentId) {
        logger.info("Fetching employees - page: {}, sortBy: {}, sortDir: {}, departmentId: {}",
                page, sortBy, sortDir, departmentId);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(resolveSortField(sortBy)).descending()
                : Sort.by(resolveSortField(sortBy)).ascending();

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, sort);

        Page<Employee> employeePage;
        if (departmentId != null) {
            employeePage = employeeRepository.findByDepartmentId(departmentId, pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        return employeePage.map(this::mapToDTO);
    }

    public EmployeeDTO getEmployeeById(Long id) {
        logger.info("Fetching employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToDTO(employee);
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        logger.info("Creating employee: {}", employeeDTO.getFullName());

        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new DuplicateResourceException(
                    "Employee already exists with email: " + employeeDTO.getEmail());
        }

        Department department = departmentService.getDepartmentEntity(employeeDTO.getDepartmentId());

        Employee employee = Employee.builder()
                .fullName(employeeDTO.getFullName())
                .email(employeeDTO.getEmail())
                .department(department)
                .salary(employeeDTO.getSalary())
                .joiningDate(employeeDTO.getJoiningDate())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Employee created successfully with ID: {}", savedEmployee.getId());

        try {
            NotificationDTO notification = NotificationDTO.builder()
                    .employeeName(savedEmployee.getFullName())
                    .employeeEmail(savedEmployee.getEmail())
                    .employeeId(savedEmployee.getId())
                    .department(department.getDepartmentName())
                    .build();
            notificationPublisher.publishEmployeeNotification(notification);
        } catch (Exception e) {
            logger.error("Failed to send employee notification, but employee was created: {}", e.getMessage());
        }

        return mapToDTO(savedEmployee);
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        logger.info("Updating employee with ID: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        if (!existingEmployee.getEmail().equals(employeeDTO.getEmail())
                && employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new DuplicateResourceException(
                    "Employee already exists with email: " + employeeDTO.getEmail());
        }

        Department department = departmentService.getDepartmentEntity(employeeDTO.getDepartmentId());

        existingEmployee.setFullName(employeeDTO.getFullName());
        existingEmployee.setEmail(employeeDTO.getEmail());
        existingEmployee.setDepartment(department);
        existingEmployee.setSalary(employeeDTO.getSalary());
        existingEmployee.setJoiningDate(employeeDTO.getJoiningDate());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        logger.info("Employee updated successfully with ID: {}", updatedEmployee.getId());
        return mapToDTO(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employeeRepository.delete(employee);
        logger.info("Employee deleted successfully with ID: {}", id);
    }

    public Employee getEmployeeEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    private String resolveSortField(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "name":
                return "fullName";
            case "department":
                return "department.departmentName";
            case "joiningdate":
            case "joining_date":
                return "joiningDate";
            default:
                return "id";
        }
    }

    private EmployeeDTO mapToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getDepartmentName())
                .salary(employee.getSalary())
                .joiningDate(employee.getJoiningDate())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
