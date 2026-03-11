package com.assessment.miniwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String transactionId,
        String type,
        String category,
        String status,
        BigDecimal amount,
        String sourceUserId,
        String destinationUserId,
        LocalDateTime timestamp
) {}