package com.ems.employeemanagementsystem.repository;

import com.ems.employeemanagementsystem.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}
