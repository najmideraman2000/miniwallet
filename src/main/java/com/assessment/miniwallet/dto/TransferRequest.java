package com.assessment.miniwallet.dto;

import java.math.BigDecimal;

public record TransferRequest(Long sourceUserId, Long destinationUserId, BigDecimal amount) {}