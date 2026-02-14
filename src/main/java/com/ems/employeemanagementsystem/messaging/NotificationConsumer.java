package com.ems.employeemanagementsystem.messaging;

import com.ems.employeemanagementsystem.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.employee}")
    public void consumeEmployeeNotification(NotificationDTO notification) {
        try {
            logger.info("========== EMPLOYEE NOTIFICATION RECEIVED ==========");
            logger.info("Type: {}", notification.getType());
            logger.info("Employee Name: {}", notification.getEmployeeName());
            logger.info("Employee Email: {}", notification.getEmployeeEmail());
            logger.info("Employee ID: {}", notification.getEmployeeId());
            logger.info("Department: {}", notification.getDepartment());
            logger.info("Purpose: {}", notification.getPurpose());
            logger.info("Timestamp: {}", notification.getTimestamp());

            simulateEmailSending(
                    notification.getEmployeeEmail(),
                    "Welcome to the Company!",
                    String.format("Dear %s,\n\nWelcome to the %s department! " +
                                    "Your Employee ID is %d.\n\nBest regards,\nHR Team",
                            notification.getEmployeeName(),
                            notification.getDepartment(),
                            notification.getEmployeeId())
            );

            logger.info("========== EMPLOYEE NOTIFICATION PROCESSED ==========");
        } catch (Exception e) {
            logger.error("Error processing employee notification: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.leave}")
    public void consumeLeaveNotification(NotificationDTO notification) {
        try {
            logger.info("========== LEAVE STATUS NOTIFICATION RECEIVED ==========");
            logger.info("Type: {}", notification.getType());
            logger.info("Employee Name: {}", notification.getEmployeeName());
            logger.info("Leave Dates: {} to {}", notification.getLeaveStartDate(), notification.getLeaveEndDate());
            logger.info("Status: {}", notification.getLeaveStatus());
            logger.info("Request ID: {}", notification.getRequestId());
            logger.info("Purpose: {}", notification.getPurpose());
            logger.info("Timestamp: {}", notification.getTimestamp());

            simulateEmailSending(
                    notification.getEmployeeEmail(),
                    "Leave Request Status Update",
                    String.format("Dear %s,\n\nYour leave request (ID: %d) from %s to %s " +
                                    "has been %s.\n\nBest regards,\nHR Team",
                            notification.getEmployeeName(),
                            notification.getRequestId(),
                            notification.getLeaveStartDate(),
                            notification.getLeaveEndDate(),
                            notification.getLeaveStatus())
            );

            logger.info("========== LEAVE STATUS NOTIFICATION PROCESSED ==========");
        } catch (Exception e) {
            logger.error("Error processing leave notification: {}", e.getMessage(), e);
        }
    }

    private void simulateEmailSending(String to, String subject, String body) {
        logger.info("--- SIMULATED EMAIL ---");
        logger.info("To: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Body:\n{}", body);
        logger.info("--- EMAIL SENT SUCCESSFULLY (SIMULATED) ---");
    }
}
