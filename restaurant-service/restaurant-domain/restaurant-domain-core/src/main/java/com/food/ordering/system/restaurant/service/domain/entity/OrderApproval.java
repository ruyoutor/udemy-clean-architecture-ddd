package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.OrderApprovalState;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

public class OrderApproval extends BaseEntity<OrderApprovalId> {

    private final RestaurantId restaurantId;
    private final OrderId orderId;
    private final OrderApprovalState orderApprovalState;

    private OrderApproval(Builder builder) {
        super(builder.orderApprovalId);
        restaurantId = builder.restaurantId;
        orderId = builder.orderId;
        orderApprovalState = builder.orderApprovalState;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderApprovalState getOrderApprovalState() {
        return orderApprovalState;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderApprovalId orderApprovalId;
        private RestaurantId restaurantId;
        private OrderId orderId;
        private OrderApprovalState orderApprovalState;

        private Builder() {
        }

        public Builder orderApprovalId(OrderApprovalId val) {
            orderApprovalId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder orderApprovalState(OrderApprovalState val) {
            orderApprovalState = val;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}
