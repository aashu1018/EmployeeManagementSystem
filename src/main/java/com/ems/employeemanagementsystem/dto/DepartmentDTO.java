package com.ems.employeemanagementsystem.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class DepartmentDTO {

    private Long id;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    private String location;

    private LocalDateTime createdAt;

    public DepartmentDTO() {
    }

    public DepartmentDTO(Long id, String departmentName, String location, LocalDateTime createdAt) {
        this.id = id;
        this.departmentName = departmentName;
        this.location = location;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String departmentName;
        private String location;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DepartmentDTO build() {
            DepartmentDTO d = new DepartmentDTO();
            d.setId(this.id);
            d.setDepartmentName(this.departmentName);
            d.setLocation(this.location);
            d.setCreatedAt(this.createdAt);
            return d;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
