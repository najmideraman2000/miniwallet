package com.assessment.miniwallet.dto;

import java.math.BigDecimal;

public record DebitRequest(
        BigDecimal amount) {}