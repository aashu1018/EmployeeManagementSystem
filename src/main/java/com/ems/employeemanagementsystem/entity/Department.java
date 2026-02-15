package com.ems.employeemanagementsystem.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @NotBlank(message = "Department name is required")
    @Column(name = "department_name", nullable = false)
    private String departmentName;

    @Column(name = "location")
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    public Department() {
    }

    public Department(Long id, String departmentName, String location, LocalDateTime createdAt, List<Employee> employees) {
        this.id = id;
        this.departmentName = departmentName;
        this.location = location;
        this.createdAt = createdAt;
        this.employees = employees != null ? employees : new ArrayList<>();
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
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder departmentName(String departmentName) {
            this.departmentName = departmentName;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Department build() {
            Department d = new Department();
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
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees != null ? employees : new ArrayList<>(); }
}
