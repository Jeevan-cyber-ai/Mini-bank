package com.banfico.mini_banking.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionRequest(
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") double amount,
        @NotBlank(message = "Description is required") String description) {
}
