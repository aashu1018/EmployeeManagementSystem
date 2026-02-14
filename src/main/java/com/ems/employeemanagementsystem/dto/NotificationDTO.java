package com.ems.employeemanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String employeeName;
    private String employeeEmail;
    private Long employeeId;
    private String department;
    private String leaveStartDate;
    private String leaveEndDate;
    private String leaveStatus;
    private Long requestId;
    private String purpose;
    private LocalDateTime timestamp;
}
