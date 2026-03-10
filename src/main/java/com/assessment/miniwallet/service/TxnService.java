package com.assessment.miniwallet.service;

import com.assessment.miniwallet.dto.TransactionResponse;
import com.assessment.miniwallet.entity.TxnHistory;
import com.assessment.miniwallet.entity.User;
import com.assessment.miniwallet.entity.TxnMaster;
import com.assessment.miniwallet.enums.TransactionCategory;
import com.assessment.miniwallet.enums.TransactionStatus;
import com.assessment.miniwallet.enums.TransactionType;
import com.assessment.miniwallet.exception.InsufficientFundsException;
import com.assessment.miniwallet.repository.TxnHistoryRepository;
import com.assessment.miniwallet.repository.TxnMasterRepository;
import com.assessment.miniwallet.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TxnService {

    private final UserRepository userRepository;
    private final TxnMasterRepository txnMasterRepository;
    private final TxnHistoryRepository txnHistoryRepository;

    public BigDecimal getBalance(Long userId) {
        return getUser(userId).getBalance();
    }

    @Transactional
    public TxnMaster credit(Long userId, BigDecimal amount) {
        log.info("Initiating CREDIT for userId: {}, amount: {}", userId, amount);
        User user = getUser(userId);

        TxnMaster transaction = saveTransaction(
                TransactionType.CREDIT,
                TransactionCategory.TOPUP,
                TransactionStatus.PENDING,
                amount,
                null,
                user
        );

        log.info("Transaction created with PENDING status. ID: {}", transaction.getId());

        try {
            processCredit(user, amount);
            transaction.setStatus(TransactionStatus.SUCCESS);
            log.info("Successfully processed CREDIT for userId: {}. Transaction ID: {}", userId, transaction.getId());
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            txnMasterRepository.save(transaction);
            log.error("Failed to process CREDIT for userId: {}. Reason: {}", userId, e.getMessage());
            throw e;
        }

        return txnMasterRepository.save(transaction);
    }

    @Transactional(noRollbackFor = InsufficientFundsException.class)
    public TxnMaster transfer(Long sourceUserId, Long destinationUserId, BigDecimal amount) {
        log.info("Initiating TRANSFER from userId: {} to userId: {}, amount: {}", sourceUserId, destinationUserId, amount);

        User source = getUser(sourceUserId);
        User destination = getUser(destinationUserId);

        TxnMaster transaction = saveTransaction(
                TransactionType.DEBIT,
                TransactionCategory.TRANSFER,
                TransactionStatus.PENDING,
                amount,
                source,
                destination
        );

        log.info("Transaction created with PENDING status. ID: {}", transaction.getId());

        try {
            processDebit(source, amount);
            processCredit(destination, amount);
            transaction.setStatus(TransactionStatus.SUCCESS);
            log.info("Successfully processed TRANSFER. Transaction ID: {}", transaction.getId());
        } catch (InsufficientFundsException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            txnMasterRepository.save(transaction);
            log.warn("Transfer FAILED due to insufficient funds for userId: {}", sourceUserId);
            throw e;
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            txnMasterRepository.save(transaction);
            log.error("Unexpected error during TRANSFER: {}", e.getMessage());
            throw e;
        }
        return txnMasterRepository.save(transaction);
    }

    @Transactional
    public void archiveCompletedTransactions() {
        log.info("Starting batch archive of completed transactions...");
        List<TxnMaster> completedTransactions = txnMasterRepository.findByStatusNot(TransactionStatus.PENDING);

        if (completedTransactions.isEmpty()) {
            log.info("No completed transactions found to archive.");
            return;
        }

        List<TxnHistory> historyRecords = completedTransactions.stream()
                .map(this::mapToHistoryRecord)
                .toList();

        txnHistoryRepository.saveAll(historyRecords);
        txnMasterRepository.deleteAllInBatch(completedTransactions);
        log.info("Successfully archived {} transactions.", historyRecords.size());
    }

    public List<TransactionResponse> getTransactionHistory(Long userId) {
        log.debug("Fetching combined transaction history for userId: {}", userId);

        List<TxnMaster> activeTxns = txnMasterRepository.findByUserIdOrderByTimestampDesc(userId);
        List<TxnHistory> archivedTxns = txnHistoryRepository.findByUserIdOrderByTimestampDesc(userId);

        List<TransactionResponse> combinedHistory = new java.util.ArrayList<>();

        activeTxns.forEach(txn -> combinedHistory.add(mapToResponseDto(txn.getId(), txn)));
        archivedTxns.forEach(txn -> combinedHistory.add(mapToResponseDto(txn.getId(), txn)));

        combinedHistory.sort(java.util.Comparator.comparing(TransactionResponse::timestamp).reversed());

        return combinedHistory;
    }

    private void processDebit(User user, BigDecimal amount) {
        validateBalance(user, amount);
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }

    private void processCredit(User user, BigDecimal amount) {
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    private void validateBalance(User user, BigDecimal amount) {
        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for user: " + user.getId());
        }
    }

    private TxnMaster saveTransaction(
            TransactionType type,
            TransactionCategory category,
            TransactionStatus status,
            BigDecimal amount,
            User source,
            User destination) {

        TxnMaster transaction = TxnMaster.builder()
                .type(type)
                .category(category)
                .status(status)
                .amount(amount)
                .sourceUser(source)
                .destinationUser(destination)
                .timestamp(LocalDateTime.now())
                .build();
        return txnMasterRepository.save(transaction);
    }

    private TxnHistory mapToHistoryRecord(TxnMaster master) {
        return TxnHistory.builder()
                .id(master.getId())
                .type(master.getType())
                .category(master.getCategory())
                .status(master.getStatus())
                .amount(master.getAmount())
                .sourceUser(master.getSourceUser())
                .destinationUser(master.getDestinationUser())
                .timestamp(master.getTimestamp())
                .build();
    }

    private TransactionResponse mapToResponseDto(Long id, com.assessment.miniwallet.entity.BaseTxn txn) {
        return new TransactionResponse(
                id,
                txn.getType().name(),
                txn.getCategory().name(),
                txn.getStatus().name(),
                txn.getAmount(),
                txn.getSourceUser() != null ? txn.getSourceUser().getId() : null,
                txn.getDestinationUser() != null ? txn.getDestinationUser().getId() : null,
                txn.getTimestamp()
        );
    }
}