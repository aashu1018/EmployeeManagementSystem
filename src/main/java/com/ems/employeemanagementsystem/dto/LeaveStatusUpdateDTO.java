package com.ems.employeemanagementsystem.dto;

import com.ems.employeemanagementsystem.entity.enums.LeaveStatus;

import javax.validation.constraints.NotNull;

public class LeaveStatusUpdateDTO {

    @NotNull(message = "Status is required")
    private LeaveStatus status;

    public LeaveStatusUpdateDTO() {
    }

    public LeaveStatusUpdateDTO(LeaveStatus status) {
        this.status = status;
    }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
}
