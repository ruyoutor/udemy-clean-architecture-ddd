package com.food.ordering.system.payment.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties
public class PaymentServiceConfigData {
    private String paymentRequestTopicName;
    private String paymentResponseTopicName;
}
