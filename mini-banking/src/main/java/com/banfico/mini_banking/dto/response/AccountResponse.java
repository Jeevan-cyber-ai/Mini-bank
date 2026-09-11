package com.banfico.mini_banking.dto.response;

import java.time.LocalDateTime;

public record AccountResponse(
                Long id,
                String accountNumber,
                String accountType,
                double balance,
                Long customerId,
                String customerName,
                LocalDateTime createdAt) {
}
