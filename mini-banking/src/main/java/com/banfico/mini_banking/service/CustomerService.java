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

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());

        Customer updated = repository.save(customer);
        return toResponse(updated);
    }

    public void deleteCustomer(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        repository.delete(customer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail());
    }

}
