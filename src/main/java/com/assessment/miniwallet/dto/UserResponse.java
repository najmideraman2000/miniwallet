package com.assessment.miniwallet.dto;

import java.math.BigDecimal;

public record UserResponse(
        String id,
        String name,
        String email,
        BigDecimal balance) {}