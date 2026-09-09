package com.banfico.mini_banking.service;

import org.springframework.stereotype.Service;
import com.banfico.mini_banking.dto.response.CustomerRequest;
import com.banfico.mini_banking.dto.response.CustomerResponse;
import com.banfico.mini_banking.entity.Customer;
import com.banfico.mini_banking.exception.ResourceNotFoundException;
import com.banfico.mini_banking.repository.CustomerRepository;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerResponse create(CustomerRequest req) {
        Customer customer = new Customer();
        customer.setFirstName(req.firstName());
        customer.setLastName(req.lastName());
        customer.setEmail(req.email());
        customer.setPhoneNumber(req.phoneNumber());
        customer.setAddress(req.address());
        Customer saved = repository.save(customer);
        return toResponse(saved);
    }

    public List<CustomerResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public CustomerResponse getById(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail());
    }

}
