package com.food.ordering.system.order.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class TrakingId extends BaseId<UUID> {

    public TrakingId(UUID value) {
        super(value);
    }
}
