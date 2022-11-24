package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.payment.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.domain.event.PaymentEvent;
import com.food.ordering.system.payment.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.ports.input.PaymentRequestMessageListener;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;
    private final PaymentCompletedMessagePublisher completedMessagePublisher;
    private final PaymentCancelledMessagePublisher cancelledMessagePublisher;
    private final PaymentFailedMessagePublisher failedMessagePublisher;

    public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper,
                                             PaymentCompletedMessagePublisher completedMessagePublisher,
                                             PaymentCancelledMessagePublisher cancelledMessagePublisher,
                                             PaymentFailedMessagePublisher failedMessagePublisher) {
        this.paymentRequestHelper = paymentRequestHelper;
        this.completedMessagePublisher = completedMessagePublisher;
        this.cancelledMessagePublisher = cancelledMessagePublisher;
        this.failedMessagePublisher = failedMessagePublisher;
    }

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        fireEvent(paymentEvent);

    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info("Publishing payment event with payment id: {} and order id: {}",
                paymentEvent.getPayment().getId().getValue(),
                paymentEvent.getPayment().getOrderId().getValue());

        if (paymentEvent instanceof PaymentCompletedEvent){
            completedMessagePublisher.publish((PaymentCompletedEvent) paymentEvent);
        } else if (paymentEvent instanceof PaymentCancelledEvent){
            cancelledMessagePublisher.publish((PaymentCancelledEvent) paymentEvent);
        } else if (paymentEvent instanceof PaymentFailedEvent){
            failedMessagePublisher.publish((PaymentFailedEvent) paymentEvent);
        }
    }
}
