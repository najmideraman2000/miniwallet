package com.assessment.miniwallet.repository;

import com.assessment.miniwallet.entity.TxnHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TxnHistoryRepository extends JpaRepository<TxnHistory, Long> {

    @Query("SELECT t FROM TxnHistory t WHERE t.sourceUser.id = :userId OR t.destinationUser.id = :userId ORDER BY t.timestamp DESC")
    List<TxnHistory> findByUserIdOrderByTimestampDesc(@Param("userId") String userId);
}