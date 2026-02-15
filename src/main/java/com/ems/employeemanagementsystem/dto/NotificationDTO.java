package com.ems.employeemanagementsystem.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

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

    public NotificationDTO() {
    }

    public NotificationDTO(String type, String employeeName, String employeeEmail, Long employeeId, String department,
                           String leaveStartDate, String leaveEndDate, String leaveStatus, Long requestId, String purpose, LocalDateTime timestamp) {
        this.type = type;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.employeeId = employeeId;
        this.department = department;
        this.leaveStartDate = leaveStartDate;
        this.leaveEndDate = leaveEndDate;
        this.leaveStatus = leaveStatus;
        this.requestId = requestId;
        this.purpose = purpose;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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

        public Builder type(String type) { this.type = type; return this; }
        public Builder employeeName(String employeeName) { this.employeeName = employeeName; return this; }
        public Builder employeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; return this; }
        public Builder employeeId(Long employeeId) { this.employeeId = employeeId; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder leaveStartDate(String leaveStartDate) { this.leaveStartDate = leaveStartDate; return this; }
        public Builder leaveEndDate(String leaveEndDate) { this.leaveEndDate = leaveEndDate; return this; }
        public Builder leaveStatus(String leaveStatus) { this.leaveStatus = leaveStatus; return this; }
        public Builder requestId(Long requestId) { this.requestId = requestId; return this; }
        public Builder purpose(String purpose) { this.purpose = purpose; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public NotificationDTO build() {
            return new NotificationDTO(type, employeeName, employeeEmail, employeeId, department,
                    leaveStartDate, leaveEndDate, leaveStatus, requestId, purpose, timestamp);
        }
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getLeaveStartDate() { return leaveStartDate; }
    public void setLeaveStartDate(String leaveStartDate) { this.leaveStartDate = leaveStartDate; }
    public String getLeaveEndDate() { return leaveEndDate; }
    public void setLeaveEndDate(String leaveEndDate) { this.leaveEndDate = leaveEndDate; }
    public String getLeaveStatus() { return leaveStatus; }
    public void setLeaveStatus(String leaveStatus) { this.leaveStatus = leaveStatus; }
    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
