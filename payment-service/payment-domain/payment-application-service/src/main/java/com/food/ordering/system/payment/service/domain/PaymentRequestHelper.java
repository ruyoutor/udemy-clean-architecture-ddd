package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private enum Operation {
        PAYMENT, CANCEL;
    }

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final PaymentCompletedMessagePublisher completedMessagePublisher;
    private final PaymentCancelledMessagePublisher cancelledMessagePublisher;
    private final PaymentFailedMessagePublisher failedMessagePublisher;

    public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                                PaymentDataMapper paymentDataMapper,
                                PaymentRepository paymentRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                CreditEntryRepository creditEntryRepository,
                                PaymentCompletedMessagePublisher completedMessagePublisher,
                                PaymentCancelledMessagePublisher cancelledMessagePublisher,
                                PaymentFailedMessagePublisher failedMessagePublisher) {

        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.completedMessagePublisher = completedMessagePublisher;
        this.cancelledMessagePublisher = cancelledMessagePublisher;
        this.failedMessagePublisher = failedMessagePublisher;
    }

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest){
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);

        PaymentEvent paymentEvent = process(Operation.PAYMENT, payment);
        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest){
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> paymentResponse = paymentRepository
                .findByOrderId(UUID.fromString(paymentRequest.getOrderId()));

        if (paymentResponse.isEmpty()){
            log.error("Payment with order id: {} could not be found!", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Payment with order id: " +
                    paymentRequest.getOrderId() + "could not be found!");
        }
        
        Payment payment = paymentResponse.get();
        PaymentEvent paymentEvent = process(Operation.CANCEL, payment);
        return paymentEvent;
    }

    private PaymentEvent process(Operation operation, Payment payment) {

        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = Lists.newArrayList();

        if (Operation.PAYMENT == operation) {
            PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(
                    payment,
                    creditEntry,
                    creditHistories,
                    failureMessages,
                    completedMessagePublisher,
                    failedMessagePublisher);
            persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
            return paymentEvent;
        }

        if (Operation.CANCEL == operation) {
            PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(
                    payment,
                    creditEntry,
                    creditHistories,
                    failureMessages,
                    cancelledMessagePublisher,
                    failedMessagePublisher);
            persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
            return paymentEvent;
        }
        throw new PaymentApplicationServiceException("Invalid operation!");
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()){
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: "
                    + customerId.getValue());
        }
        return creditEntry.get();
    }

    private List<CreditHistory> getCreditHistories(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistories.isEmpty()){
            log.error("Could not find credit history for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit history for customer: "
                    + customerId.getValue());
        }
        return creditHistories.get();
    }

    private void persistDbObjects(Payment payment,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories,
                                  List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()){
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }
}
