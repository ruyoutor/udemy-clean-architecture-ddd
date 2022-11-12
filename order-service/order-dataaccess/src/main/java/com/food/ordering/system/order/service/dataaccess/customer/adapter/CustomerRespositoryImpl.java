package com.food.ordering.system.order.service.dataaccess.customer.adapter;

import com.food.ordering.system.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRespositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataMapper;

    public CustomerRespositoryImpl(CustomerJpaRepository customerJpaRepository,
                                   CustomerDataAccessMapper customerDataMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerDataMapper = customerDataMapper;
    }

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).
                map(customerDataMapper::customerEntityToCustomer);
    }
}
