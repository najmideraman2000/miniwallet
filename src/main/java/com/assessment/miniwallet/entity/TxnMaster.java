package com.assessment.miniwallet.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "txn_master")
@Getter @Setter @NoArgsConstructor @SuperBuilder
public class TxnMaster extends BaseTxn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}