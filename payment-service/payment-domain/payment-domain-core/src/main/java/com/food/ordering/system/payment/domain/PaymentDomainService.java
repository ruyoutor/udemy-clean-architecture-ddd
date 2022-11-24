package com.food.ordering.system.payment.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.domain.entity.CreditEntry;
import com.food.ordering.system.payment.domain.entity.CreditHistory;
import com.food.ordering.system.payment.domain.entity.Payment;
import com.food.ordering.system.payment.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.domain.event.PaymentEvent;
import com.food.ordering.system.payment.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages,
                                            DomainEventPublisher<PaymentCompletedEvent> completedEventPublisher,
                                            DomainEventPublisher<PaymentFailedEvent> failedEventPublisher);

    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages,
                                          DomainEventPublisher<PaymentCancelledEvent> cancelledEventPublisher,
                                          DomainEventPublisher<PaymentFailedEvent> failedEventPublisher);
}
