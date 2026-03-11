package com.assessment.miniwallet.repository;

import com.assessment.miniwallet.entity.TxnMaster;
import com.assessment.miniwallet.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TxnMasterRepository extends JpaRepository<TxnMaster, Long> {

    @Query(value = "SELECT nextval('txn_ref_seq')", nativeQuery = true)
    Long getNextReferenceSequence();

    @Query("SELECT t FROM TxnMaster t WHERE t.sourceUser.id = :userId OR t.destinationUser.id = :userId ORDER BY t.timestamp DESC")
    List<TxnMaster> findByUserIdOrderByTimestampDesc(@Param("userId") String userId);

    List<TxnMaster> findByStatusNot(TransactionStatus status);
}