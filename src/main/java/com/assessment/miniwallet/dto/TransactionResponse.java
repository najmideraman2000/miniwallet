package com.assessment.miniwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        String type,
        String category,
        String status,
        BigDecimal amount,
        Long sourceUserId,
        Long destinationUserId,
        LocalDateTime timestamp
) {}