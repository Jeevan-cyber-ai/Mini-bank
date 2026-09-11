package com.banfico.mini_banking.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banfico.mini_banking.dto.response.TransactionRequest;
import com.banfico.mini_banking.dto.response.TransactionResponse;
import com.banfico.mini_banking.entity.Account;
import com.banfico.mini_banking.entity.Transaction;
import com.banfico.mini_banking.entity.Transtype;
import com.banfico.mini_banking.exception.ResourceNotFoundException;
import com.banfico.mini_banking.repository.AccountRepository;
import com.banfico.mini_banking.repository.TransactionRepository;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse deposit(String accountNumber, TransactionRequest transactionReq) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        account.setBalance(account.getBalance() + transactionReq.amount());
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .accountNumber(accountNumber)
                .type(Transtype.CREDIT)
                .amount(transactionReq.amount())
                .balanceAfter(account.getBalance())
                .description(transactionReq.description())
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, TransactionRequest transactionReq) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        if (account.getBalance() < transactionReq.amount()) {
            throw new IllegalStateException("Insufficient balance for withdrawal.");
        }

        account.setBalance(account.getBalance() - transactionReq.amount());
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .accountNumber(accountNumber)
                .type(Transtype.DEBIT)
                .amount(transactionReq.amount())
                .balanceAfter(account.getBalance())
                .description(transactionReq.description())
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getHistory(String accountNumber) {
        accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        return transactionRepository
                .findByAccountNumber(accountNumber)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                accountRepository.findByAccountNumber(t.getAccountNumber()).get().getBalance(),
                t.getDescription(),
                t.getAccountNumber(),
                t.getCreatedAt());
    }
}
