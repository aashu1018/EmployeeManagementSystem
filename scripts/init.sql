-- Employee Management System - Database Initialization Script
-- This script runs automatically when the MySQL container starts for the first time

CREATE DATABASE IF NOT EXISTS ems_db;
USE ems_db;

-- Grant privileges to the application user
GRANT ALL PRIVILEGES ON ems_db.* TO 'ems_user'@'%';
FLUSH PRIVILEGES;

-- Note: Tables are auto-created by Hibernate (ddl-auto: update)
-- Below are the table definitions for reference

-- Departments table
CREATE TABLE IF NOT EXISTS departments (
    department_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employees table
CREATE TABLE IF NOT EXISTS employees (
    employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    salary DECIMAL(12,2),
    joining_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- Leave requests table
CREATE TABLE IF NOT EXISTS leave_requests (
    leave_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Insert sample departments
INSERT INTO departments (department_name, location) VALUES
    ('Engineering', 'Building A, Floor 3'),
    ('Human Resources', 'Building B, Floor 1'),
    ('Marketing', 'Building A, Floor 2'),
    ('Finance', 'Building C, Floor 1'),
    ('Operations', 'Building B, Floor 2');

-- Insert sample employees
INSERT INTO employees (full_name, email, department_id, salary, joining_date) VALUES
    ('John Smith', 'john.smith@company.com', 1, 85000.00, '2023-01-15'),
    ('Jane Doe', 'jane.doe@company.com', 1, 92000.00, '2022-06-20'),
    ('Bob Johnson', 'bob.johnson@company.com', 2, 75000.00, '2023-03-10'),
    ('Alice Williams', 'alice.williams@company.com', 3, 78000.00, '2023-07-01'),
    ('Charlie Brown', 'charlie.brown@company.com', 4, 88000.00, '2022-11-15'),
    ('Diana Prince', 'diana.prince@company.com', 5, 82000.00, '2023-02-28'),
    ('Edward Norton', 'edward.norton@company.com', 1, 95000.00, '2021-09-01'),
    ('Fiona Apple', 'fiona.apple@company.com', 2, 71000.00, '2023-05-15'),
    ('George Lucas', 'george.lucas@company.com', 3, 80000.00, '2022-08-20'),
    ('Hannah Montana', 'hannah.montana@company.com', 4, 76000.00, '2023-04-10'),
    ('Ivan Drago', 'ivan.drago@company.com', 5, 79000.00, '2023-06-01'),
    ('Julia Roberts', 'julia.roberts@company.com', 1, 91000.00, '2022-01-10');

-- Insert sample leave requests
INSERT INTO leave_requests (employee_id, start_date, end_date, status, reason) VALUES
    (1, '2024-03-15', '2024-03-20', 'APPROVED', 'Family vacation'),
    (2, '2024-04-01', '2024-04-05', 'PENDING', 'Personal leave'),
    (3, '2024-03-25', '2024-03-27', 'REJECTED', 'Short trip'),
    (1, '2024-05-10', '2024-05-15', 'PENDING', 'Medical appointment');
