package com.assessment.miniwallet.controller;

import com.assessment.miniwallet.dto.CreditRequest;
import com.assessment.miniwallet.dto.TransactionResponse;
import com.assessment.miniwallet.dto.TransferRequest;
import com.assessment.miniwallet.entity.TxnMaster;
import com.assessment.miniwallet.service.TxnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class TxnController {

    private final TxnService txnService;

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {
        log.info("Received request to fetch balance for userId: {}", userId);
        return ResponseEntity.ok(txnService.getBalance(userId));
    }

    @PostMapping("/{userId}/top-up")
    public ResponseEntity<TxnMaster> credit(@PathVariable Long userId, @RequestBody CreditRequest request) {
        log.info("Received top-up request for userId: {}, amount: {}", userId, request.amount());
        return ResponseEntity.ok(txnService.credit(userId, request.amount()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TxnMaster> transfer(@RequestBody TransferRequest request) {
        log.info("Received transfer request from userId: {} to userId: {}, amount: {}",
                request.sourceUserId(), request.destinationUserId(), request.amount());
        return ResponseEntity.ok(txnService.transfer(
                request.sourceUserId(), request.destinationUserId(), request.amount()));
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(@PathVariable Long userId) {
        log.info("Received request to fetch transaction history for userId: {}", userId);
        return ResponseEntity.ok(txnService.getTransactionHistory(userId));
    }
}