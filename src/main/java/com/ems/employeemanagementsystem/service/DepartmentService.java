package com.ems.employeemanagementsystem.service;

import com.ems.employeemanagementsystem.dto.DepartmentDTO;
import com.ems.employeemanagementsystem.dto.EmployeeDTO;
import com.ems.employeemanagementsystem.entity.Department;
import com.ems.employeemanagementsystem.entity.Employee;
import com.ems.employeemanagementsystem.exception.DuplicateResourceException;
import com.ems.employeemanagementsystem.exception.ResourceNotFoundException;
import com.ems.employeemanagementsystem.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<DepartmentDTO> getAllDepartments() {
        logger.info("Fetching all departments");
        return departmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO getDepartmentById(Long id) {
        logger.info("Fetching department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return mapToDTO(department);
    }

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        logger.info("Creating department: {}", departmentDTO.getDepartmentName());

        if (departmentRepository.existsByDepartmentName(departmentDTO.getDepartmentName())) {
            throw new DuplicateResourceException(
                    "Department already exists with name: " + departmentDTO.getDepartmentName());
        }

        Department department = Department.builder()
                .departmentName(departmentDTO.getDepartmentName())
                .location(departmentDTO.getLocation())
                .build();

        Department savedDepartment = departmentRepository.save(department);
        logger.info("Department created successfully with ID: {}", savedDepartment.getId());
        return mapToDTO(savedDepartment);
    }

    public List<EmployeeDTO> getDepartmentEmployees(Long departmentId) {
        logger.info("Fetching employees for department ID: {}", departmentId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));

        return department.getEmployees()
                .stream()
                .map(this::mapEmployeeToDTO)
                .collect(Collectors.toList());
    }

    public Department getDepartmentEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    private DepartmentDTO mapToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .location(department.getLocation())
                .createdAt(department.getCreatedAt())
                .build();
    }

    private EmployeeDTO mapEmployeeToDTO(Employee employee) {
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
