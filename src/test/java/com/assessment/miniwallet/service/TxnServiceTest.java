package com.assessment.miniwallet.service;

import com.assessment.miniwallet.dto.TransactionResponse;
import com.assessment.miniwallet.entity.TxnMaster;
import com.assessment.miniwallet.entity.User;
import com.assessment.miniwallet.enums.TransactionCategory;
import com.assessment.miniwallet.enums.TransactionStatus;
import com.assessment.miniwallet.enums.TransactionType;
import com.assessment.miniwallet.exception.InsufficientFundsException;
import com.assessment.miniwallet.repository.TxnHistoryRepository;
import com.assessment.miniwallet.repository.TxnMasterRepository;
import com.assessment.miniwallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TxnServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TxnMasterRepository txnMasterRepository;

    @Mock
    private TxnHistoryRepository txnHistoryRepository;

    @InjectMocks
    private TxnService txnService;

    private User sourceUser;
    private User destinationUser;

    @BeforeEach
    void setUp() {
        sourceUser = User.builder()
                .id("USER-001")
                .name("Alice")
                .email("alice@test.com")
                .balance(new BigDecimal("100.00"))
                .build();

        destinationUser = User.builder()
                .id("USER-002")
                .name("Bob")
                .email("bob@test.com")
                .balance(new BigDecimal("50.00"))
                .build();
    }

    @Test
    void testCredit_Success() {
        BigDecimal amount = new BigDecimal("50.00");
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(sourceUser));
        when(txnMasterRepository.getNextReferenceSequence()).thenReturn(1L);
        when(txnMasterRepository.save(any(TxnMaster.class))).thenAnswer(invocation -> {
            TxnMaster saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        TransactionResponse response = txnService.credit("USER-001", amount);

        assertNotNull(response);
        assertEquals(TransactionStatus.SUCCESS.name(), response.status());
        assertEquals(TransactionType.CREDIT.name(), response.type());
        assertEquals(new BigDecimal("150.00"), sourceUser.getBalance());
        verify(userRepository, times(1)).save(sourceUser);
        verify(txnMasterRepository, times(2)).save(any(TxnMaster.class));
    }

    @Test
    void testTransfer_Success() {
        BigDecimal amount = new BigDecimal("30.00");
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(sourceUser));
        when(userRepository.findById("USER-002")).thenReturn(Optional.of(destinationUser));
        when(txnMasterRepository.getNextReferenceSequence()).thenReturn(2L);
        when(txnMasterRepository.save(any(TxnMaster.class))).thenAnswer(invocation -> {
            TxnMaster saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        TransactionResponse response = txnService.transfer("USER-001", "USER-002", amount);

        assertNotNull(response);
        assertEquals(TransactionStatus.SUCCESS.name(), response.status());
        assertEquals(new BigDecimal("70.00"), sourceUser.getBalance());
        assertEquals(new BigDecimal("80.00"), destinationUser.getBalance());
        verify(userRepository, times(1)).save(sourceUser);
        verify(userRepository, times(1)).save(destinationUser);
    }

    @Test
    void testTransfer_InsufficientFunds() {
        BigDecimal amount = new BigDecimal("200.00");
        when(userRepository.findById("USER-001")).thenReturn(Optional.of(sourceUser));
        when(userRepository.findById("USER-002")).thenReturn(Optional.of(destinationUser));
        when(txnMasterRepository.getNextReferenceSequence()).thenReturn(3L);
        when(txnMasterRepository.save(any(TxnMaster.class))).thenAnswer(invocation -> {
            TxnMaster saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> txnService.transfer("USER-001", "USER-002", amount)
        );

        assertEquals("Insufficient funds for user: USER-001", exception.getMessage());
        assertEquals(new BigDecimal("100.00"), sourceUser.getBalance());
        assertEquals(new BigDecimal("50.00"), destinationUser.getBalance());
    }
}