package com.assessment.miniwallet.entity;

import com.assessment.miniwallet.enums.TransactionCategory;
import com.assessment.miniwallet.enums.TransactionStatus;
import com.assessment.miniwallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public abstract class BaseTxn {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id", updatable = false)
    private User sourceUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_user_id", updatable = false)
    private User destinationUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}