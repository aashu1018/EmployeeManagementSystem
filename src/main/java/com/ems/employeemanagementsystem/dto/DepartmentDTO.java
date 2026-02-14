package com.ems.employeemanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {

    private Long id;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    private String location;

    private LocalDateTime createdAt;
}
