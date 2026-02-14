package com.ems.employeemanagementsystem.service;

import com.ems.employeemanagementsystem.dto.NotificationDTO;
import com.ems.employeemanagementsystem.messaging.NotificationPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    @Test
    @DisplayName("Should publish employee notification successfully")
    void publishEmployeeNotification_ShouldPublish() {
        ReflectionTestUtils.setField(notificationPublisher, "exchangeName", "test.exchange");
        ReflectionTestUtils.setField(notificationPublisher, "employeeRoutingKey", "test.employee.key");

        NotificationDTO notification = NotificationDTO.builder()
                .employeeName("John Smith")
                .employeeEmail("john@test.com")
                .employeeId(1L)
                .department("Engineering")
                .build();

        notificationPublisher.publishEmployeeNotification(notification);

        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("test.exchange"), eq("test.employee.key"), any(NotificationDTO.class));
    }

    @Test
    @DisplayName("Should handle AMQP exception during employee notification")
    void publishEmployeeNotification_ShouldHandleAmqpException() {
        ReflectionTestUtils.setField(notificationPublisher, "exchangeName", "test.exchange");
        ReflectionTestUtils.setField(notificationPublisher, "employeeRoutingKey", "test.employee.key");

        doThrow(new AmqpException("Connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(NotificationDTO.class));

        NotificationDTO notification = NotificationDTO.builder()
                .employeeName("John Smith")
                .employeeEmail("john@test.com")
                .employeeId(1L)
                .department("Engineering")
                .build();

        // Should not throw exception
        notificationPublisher.publishEmployeeNotification(notification);
    }

    @Test
    @DisplayName("Should publish leave status notification successfully")
    void publishLeaveStatusNotification_ShouldPublish() {
        ReflectionTestUtils.setField(notificationPublisher, "exchangeName", "test.exchange");
        ReflectionTestUtils.setField(notificationPublisher, "leaveRoutingKey", "test.leave.key");

        NotificationDTO notification = NotificationDTO.builder()
                .employeeName("John Smith")
                .employeeEmail("john@test.com")
                .leaveStartDate("2024-03-15")
                .leaveEndDate("2024-03-20")
                .leaveStatus("APPROVED")
                .requestId(1L)
                .build();

        notificationPublisher.publishLeaveStatusNotification(notification);

        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("test.exchange"), eq("test.leave.key"), any(NotificationDTO.class));
    }

    @Test
    @DisplayName("Should handle AMQP exception during leave notification")
    void publishLeaveStatusNotification_ShouldHandleAmqpException() {
        ReflectionTestUtils.setField(notificationPublisher, "exchangeName", "test.exchange");
        ReflectionTestUtils.setField(notificationPublisher, "leaveRoutingKey", "test.leave.key");

        doThrow(new AmqpException("Connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(NotificationDTO.class));

        NotificationDTO notification = NotificationDTO.builder()
                .employeeName("John Smith")
                .requestId(1L)
                .build();

        // Should not throw exception
        notificationPublisher.publishLeaveStatusNotification(notification);
    }
}
