package com.assessment.miniwallet.dto;

import java.math.BigDecimal;

public record TransferRequest(
        String sourceUserId,
        String destinationUserId,
        BigDecimal amount) {}