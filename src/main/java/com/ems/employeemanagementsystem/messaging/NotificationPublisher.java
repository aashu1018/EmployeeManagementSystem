package com.ems.employeemanagementsystem.messaging;

import com.ems.employeemanagementsystem.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationPublisher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.employee}")
    private String employeeRoutingKey;

    @Value("${rabbitmq.routing-key.leave}")
    private String leaveRoutingKey;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEmployeeNotification(NotificationDTO notification) {
        try {
            notification.setTimestamp(LocalDateTime.now());
            notification.setType("EMPLOYEE_CREATED");
            notification.setPurpose("Welcome notification");

            logger.info("Publishing employee notification for: {}", notification.getEmployeeName());
            rabbitTemplate.convertAndSend(exchangeName, employeeRoutingKey, notification);
            logger.info("Successfully published employee notification for employee ID: {}",
                    notification.getEmployeeId());
        } catch (AmqpException e) {
            logger.error("Failed to publish employee notification for: {}. Error: {}",
                    notification.getEmployeeName(), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error publishing employee notification: {}", e.getMessage(), e);
        }
    }

    public void publishLeaveStatusNotification(NotificationDTO notification) {
        try {
            notification.setTimestamp(LocalDateTime.now());
            notification.setType("LEAVE_STATUS_UPDATED");
            notification.setPurpose("Status update notification");

            logger.info("Publishing leave status notification for request ID: {}", notification.getRequestId());
            rabbitTemplate.convertAndSend(exchangeName, leaveRoutingKey, notification);
            logger.info("Successfully published leave status notification for request ID: {}",
                    notification.getRequestId());
        } catch (AmqpException e) {
            logger.error("Failed to publish leave status notification for request ID: {}. Error: {}",
                    notification.getRequestId(), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error publishing leave status notification: {}", e.getMessage(), e);
        }
    }
}
