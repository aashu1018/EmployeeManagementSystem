package com.ems.employeemanagementsystem.dto;

import com.ems.employeemanagementsystem.entity.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveStatusUpdateDTO {

    @NotNull(message = "Status is required")
    private LeaveStatus status;
}
