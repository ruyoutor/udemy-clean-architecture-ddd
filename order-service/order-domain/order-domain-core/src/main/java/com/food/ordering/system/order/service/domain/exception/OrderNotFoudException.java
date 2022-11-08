package com.food.ordering.system.order.service.domain.exception;

import com.food.ordering.system.domain.exception.DomainException;

public class OrderNotFoudException extends DomainException {

    public OrderNotFoudException(String message) {
        super(message);
    }

    public OrderNotFoudException(String message, Throwable cause) {
        super(message, cause);
    }
}
