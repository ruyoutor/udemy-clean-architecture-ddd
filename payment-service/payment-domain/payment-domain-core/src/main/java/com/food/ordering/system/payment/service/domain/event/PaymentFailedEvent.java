package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentFailedEvent> publisher;

    public PaymentFailedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages,
                              DomainEventPublisher<PaymentFailedEvent> failedEventPublisher) {
        super(payment, createdAt, failureMessages);
        publisher = failedEventPublisher;
    }

    @Override
    public void fire() {
        publisher.publish(this);
    }
}
