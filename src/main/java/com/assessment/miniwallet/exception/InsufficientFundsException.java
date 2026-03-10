package com.assessment.miniwallet.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) { super(message); }
}