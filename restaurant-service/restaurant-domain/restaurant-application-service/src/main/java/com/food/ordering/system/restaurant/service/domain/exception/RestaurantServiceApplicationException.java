package com.food.ordering.system.restaurant.service.domain.exception;

import com.food.ordering.system.domain.exception.DomainException;

public class RestaurantServiceApplicationException extends DomainException {

    public RestaurantServiceApplicationException(String message) {
        super(message);
    }

    public RestaurantServiceApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
