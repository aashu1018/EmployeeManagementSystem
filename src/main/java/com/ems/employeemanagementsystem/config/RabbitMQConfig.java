package com.ems.employeemanagementsystem.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queue.employee}")
    private String employeeQueueName;

    @Value("${rabbitmq.queue.leave}")
    private String leaveQueueName;

    @Value("${rabbitmq.routing-key.employee}")
    private String employeeRoutingKey;

    @Value("${rabbitmq.routing-key.leave}")
    private String leaveRoutingKey;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue employeeNotificationQueue() {
        return QueueBuilder.durable(employeeQueueName).build();
    }

    @Bean
    public Queue leaveNotificationQueue() {
        return QueueBuilder.durable(leaveQueueName).build();
    }

    @Bean
    public Binding employeeBinding(Queue employeeNotificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(employeeNotificationQueue)
                .to(exchange)
                .with(employeeRoutingKey);
    }

    @Bean
    public Binding leaveBinding(Queue leaveNotificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(leaveNotificationQueue)
                .to(exchange)
                .with(leaveRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
