package com.ems.employeemanagementsystem.dto;

import com.ems.employeemanagementsystem.entity.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestDTO {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeName;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private LeaveStatus status;

    private String reason;

    private LocalDateTime createdAt;
}
