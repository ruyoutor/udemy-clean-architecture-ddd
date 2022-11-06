package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.exception.DomainException;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddres;
import com.food.ordering.system.order.service.domain.valueobject.TrakingId;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddres streetAddres;
    private final Money price;
    private final List<OrderItem> items;

    private TrakingId trakingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    public void initializeOrder(){
        setId(new OrderId(UUID.randomUUID()));
        trakingId = new TrakingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder(){
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    public void pay(){
        if (orderStatus != OrderStatus.PENDING){
            throw new OrderDomainException("Order is not in correct status for pay operation!");
        }
        orderStatus = OrderStatus.PAID;
    }

    public void approve(){
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in correct status for approve operation!");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages){
        if (orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order is not in correct status for initialize cancel operation!");
        }
        orderStatus = OrderStatus.CANCELING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages){
        if (!(orderStatus == OrderStatus.CANCELING || orderStatus == OrderStatus.PENDING)){
            throw new OrderDomainException("Order is not in correct status for cancel operation!");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);

    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null){
            this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
        }
        if (this.failureMessages == null){
            this.failureMessages = failureMessages;
        }
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)){
            throw new DomainException(String.format("Total price: %s is not equal to Order items total: %s!",
                    price.getAmount(), orderItemsTotal.getAmount()));
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()){
            throw new OrderDomainException(String.format("Order item price: %s is not valid for product %s",
                    orderItem.getPrice().getAmount(), orderItem.getProduct().getPrice().getAmount()));
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()){
            throw new OrderDomainException("Total price must be more than zero!");
        }
    }

    private void validateInitialOrder() {
        if (this.orderStatus != null || getId() != null){
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem: items){
            orderItem.initializeOrdemItem(super.getId(), new OrderItemId(itemId++));
        }
    }

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
