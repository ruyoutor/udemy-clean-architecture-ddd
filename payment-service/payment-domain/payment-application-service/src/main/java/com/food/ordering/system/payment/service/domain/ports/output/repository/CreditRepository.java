package com.food.ordering.system.payment.service.domain.ports.output.repository;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.domain.entity.CreditEntry;

import java.util.Optional;
import java.util.UUID;

public interface CreditRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
