package com.ems.employeemanagementsystem.repository;

import com.ems.employeemanagementsystem.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Employee> findAll(Pageable pageable);
}
