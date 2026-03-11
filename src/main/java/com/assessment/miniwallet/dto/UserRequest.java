package com.assessment.miniwallet.dto;

public record UserRequest(
        String id,
        String name,
        String email) {}