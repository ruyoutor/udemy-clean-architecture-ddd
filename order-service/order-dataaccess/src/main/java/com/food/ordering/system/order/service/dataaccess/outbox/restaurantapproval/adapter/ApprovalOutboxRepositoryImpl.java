package com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.adapter;

import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException;
import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.mapper.ApprovalOutboxDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.repository.ApprovalOutboxJpaRepository;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ApprovalOutboxRepositoryImpl implements ApprovalOutboxRepository {

    private final ApprovalOutboxJpaRepository jpaRepository;
    private final ApprovalOutboxDataAccessMapper dataMapper;

    public ApprovalOutboxRepositoryImpl(ApprovalOutboxJpaRepository jpaRepository,
                                        ApprovalOutboxDataAccessMapper dataMapper) {
        this.jpaRepository = jpaRepository;
        this.dataMapper = dataMapper;
    }

    @Override
    public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        return dataMapper.approvalOutboxEntityToOrderApprovalPaymentOutboxMessage(
                jpaRepository.save(dataMapper.orderApprovalOutboxMessageToOutboxEntity(orderApprovalOutboxMessage)));
    }

    @Override
    public Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType,
                                                                                            OutboxStatus outboxStatus,
                                                                                            SagaStatus... sagaStatus) {
        return Optional.of(
                    jpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(sagaType,
                                    outboxStatus,
                                    Arrays.asList(sagaStatus))
                            .orElseThrow(
                                    () -> new ApprovalOutboxNotFoundException("Approval outbox object " +
                                            "could not be found for saga type " + sagaType))
                            .stream()
                            .map(dataMapper::approvalOutboxEntityToOrderApprovalPaymentOutboxMessage)
                            .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String sagaType,
                                                                                UUID sagaId,
                                                                                SagaStatus... sagaStatus) {
        return jpaRepository
                .findByTypeAndSagaIdAndSagaStatusIn(sagaType, sagaId, Arrays.asList(sagaStatus))
                .map(dataMapper::approvalOutboxEntityToOrderApprovalPaymentOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus,
                                                         SagaStatus... sagaStatus) {
        jpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus, Arrays.asList(sagaStatus));
    }
}
