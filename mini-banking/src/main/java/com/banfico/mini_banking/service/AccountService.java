package com.banfico.mini_banking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.banfico.mini_banking.dto.response.AccountRequest;
import com.banfico.mini_banking.dto.response.AccountResponse;

import com.banfico.mini_banking.entity.Account;
import com.banfico.mini_banking.entity.Customer;

import com.banfico.mini_banking.exception.ResourceNotFoundException;
import com.banfico.mini_banking.repository.AccountRepository;
import com.banfico.mini_banking.repository.CustomerRepository;

@Service
public class AccountService {
    private final AccountRepository repository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository repository, CustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;

    }

    private String generateAccountNumber() {
        long number = 1000000000L + (long) (Math.random() * 9000000000L);
        return String.valueOf(number);
    }

    public AccountResponse create(AccountRequest req) {
        Customer customer = customerRepository.findById(req.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + req.customerId()));

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(req.accountType().name());
        account.setBalance(req.initialDeposit());
        account.setCustomer(customer);

        return toResponse(repository.save(account));
    }

    public List<AccountResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AccountResponse getByAccountNumber(String accountNumber) {
        Account acc = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        return toResponse(acc);
    }

    public AccountResponse updateAccount(String accountNumber, AccountRequest request) {
        Account acc = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        acc.setAccountType(request.accountType().name());
        acc.setBalance(request.initialDeposit());
        return toResponse(repository.save(acc));
    }

    public void deleteAccount(String accountNumber) {
        Account account = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        repository.delete(account);
    }

    private AccountResponse toResponse(Account acc) {
        return new AccountResponse(
                acc.getId(),
                acc.getAccountNumber(),
                acc.getAccountType(),
                acc.getBalance(),
                acc.getCustomer().getId(),
                acc.getCustomer().getFirstName() + " " + acc.getCustomer().getLastName(),
                acc.getCreatedAt());
    }

}
