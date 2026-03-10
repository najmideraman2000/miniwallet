package com.assessment.miniwallet.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "txn_history")
@Getter @Setter @NoArgsConstructor @SuperBuilder
public class TxnHistory extends BaseTxn {

    @Id
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "move_date", nullable = false, updatable = false)
    private LocalDateTime moveDate;

    @PrePersist
    protected void onMove() {
        this.moveDate = LocalDateTime.now();
    }
}