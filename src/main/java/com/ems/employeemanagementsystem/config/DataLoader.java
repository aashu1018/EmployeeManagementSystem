package com.ems.employeemanagementsystem.config;

import com.ems.employeemanagementsystem.entity.Department;
import com.ems.employeemanagementsystem.entity.Employee;
import com.ems.employeemanagementsystem.entity.LeaveRequest;
import com.ems.employeemanagementsystem.entity.enums.LeaveStatus;
import com.ems.employeemanagementsystem.repository.DepartmentRepository;
import com.ems.employeemanagementsystem.repository.EmployeeRepository;
import com.ems.employeemanagementsystem.repository.LeaveRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

// Loads sample data on startup when the database is empty. Excluded from test profile.
@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public DataLoader(DepartmentRepository departmentRepository,
                      EmployeeRepository employeeRepository,
                      LeaveRequestRepository leaveRequestRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            logger.info("Data already exists, skipping data loading");
            return;
        }

        logger.info("Loading sample data...");

        Department engineering = departmentRepository.save(
                Department.builder().departmentName("Engineering").location("Building A, Floor 3").build());
        Department hr = departmentRepository.save(
                Department.builder().departmentName("Human Resources").location("Building B, Floor 1").build());
        Department marketing = departmentRepository.save(
                Department.builder().departmentName("Marketing").location("Building A, Floor 2").build());
        Department finance = departmentRepository.save(
                Department.builder().departmentName("Finance").location("Building C, Floor 1").build());
        Department operations = departmentRepository.save(
                Department.builder().departmentName("Operations").location("Building B, Floor 2").build());

        Employee emp1 = employeeRepository.save(Employee.builder()
                .fullName("John Smith").email("john.smith@company.com")
                .department(engineering).salary(new BigDecimal("85000.00"))
                .joiningDate(LocalDate.of(2023, 1, 15)).build());

        Employee emp2 = employeeRepository.save(Employee.builder()
                .fullName("Jane Doe").email("jane.doe@company.com")
                .department(engineering).salary(new BigDecimal("92000.00"))
                .joiningDate(LocalDate.of(2022, 6, 20)).build());

        Employee emp3 = employeeRepository.save(Employee.builder()
                .fullName("Bob Johnson").email("bob.johnson@company.com")
                .department(hr).salary(new BigDecimal("75000.00"))
                .joiningDate(LocalDate.of(2023, 3, 10)).build());

        employeeRepository.save(Employee.builder()
                .fullName("Alice Williams").email("alice.williams@company.com")
                .department(marketing).salary(new BigDecimal("78000.00"))
                .joiningDate(LocalDate.of(2023, 7, 1)).build());

        employeeRepository.save(Employee.builder()
                .fullName("Charlie Brown").email("charlie.brown@company.com")
                .department(finance).salary(new BigDecimal("88000.00"))
                .joiningDate(LocalDate.of(2022, 11, 15)).build());

        employeeRepository.save(Employee.builder()
                .fullName("Diana Prince").email("diana.prince@company.com")
                .department(operations).salary(new BigDecimal("82000.00"))
                .joiningDate(LocalDate.of(2023, 2, 28)).build());

        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(emp1).startDate(LocalDate.of(2024, 3, 15))
                .endDate(LocalDate.of(2024, 3, 20)).status(LeaveStatus.APPROVED)
                .reason("Family vacation").build());

        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(emp2).startDate(LocalDate.of(2024, 4, 1))
                .endDate(LocalDate.of(2024, 4, 5)).status(LeaveStatus.PENDING)
                .reason("Personal leave").build());

        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(emp3).startDate(LocalDate.of(2024, 3, 25))
                .endDate(LocalDate.of(2024, 3, 27)).status(LeaveStatus.REJECTED)
                .reason("Short trip").build());

        logger.info("Sample data loaded successfully!");
    }
}
