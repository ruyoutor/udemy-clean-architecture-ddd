package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddres;
import com.food.ordering.system.order.service.domain.valueobject.TrakingId;

import java.util.Collections;
import java.util.List;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddres streetAddres;
    private final Money price;
    private final List<OrderItem> items;

    private TrakingId trakingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    private Order(Builder builder) {
        super(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        streetAddres = builder.streetAddres;
        price = builder.price;
        items = builder.items;
        trakingId = builder.trakingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddres getStreetAddres() {
        return streetAddres;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        if (this.items != null){
            return Collections.unmodifiableList(this.items);
        }
        return items;
    }

    public TrakingId getTrakingId() {
        return trakingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        if (this.failureMessages != null) {
            return Collections.unmodifiableList(this.failureMessages);
        }
        return failureMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddres streetAddres;
        private Money price;
        private List<OrderItem> items;
        private TrakingId trakingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder streetAddres(StreetAddres val) {
            streetAddres = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trakingId(TrakingId val) {
            trakingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
