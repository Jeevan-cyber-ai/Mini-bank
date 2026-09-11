package com.banfico.mini_banking.dto.response;

import java.time.LocalDateTime;

import com.banfico.mini_banking.entity.Transtype;

public record TransactionResponse(
        Long id,
        Transtype type,
        double amount,
        double balanceAfter,
        String description,
        String accountNumber,
        LocalDateTime createdAt) {
}
