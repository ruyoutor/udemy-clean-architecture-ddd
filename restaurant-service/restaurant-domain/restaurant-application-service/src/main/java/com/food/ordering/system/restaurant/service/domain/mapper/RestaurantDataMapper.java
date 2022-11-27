package com.food.ordering.system.restaurant.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {

    public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest
                                                                             restaurantApprovalRequest) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(getOrderDetail(restaurantApprovalRequest))
                .build();
    }

    private OrderDetail getOrderDetail(RestaurantApprovalRequest restaurantApprovalRequest) {
        return OrderDetail.builder()
                .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                .products(getProducts(restaurantApprovalRequest))
                .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                .build();
    }

    private List<Product> getProducts(RestaurantApprovalRequest restaurantApprovalRequest) {
        return restaurantApprovalRequest.getProducts().stream().map(
                product -> Product.builder()
                        .productId(product.getId())
                        .quantity(product.getQuantity())
                        .build()
        ).collect(Collectors.toList());
    }
}
