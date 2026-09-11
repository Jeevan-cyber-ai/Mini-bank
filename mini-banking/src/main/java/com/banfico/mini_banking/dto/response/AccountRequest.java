package com.banfico.mini_banking.dto.response; // or dto.request

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import com.banfico.mini_banking.entity.AccountType;

public record AccountRequest(
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotNull(message = "Account type is required (SAVING or CURRENT)") AccountType accountType,
        @PositiveOrZero(message = "Initial deposit cannot be negative") double initialDeposit) {
}
