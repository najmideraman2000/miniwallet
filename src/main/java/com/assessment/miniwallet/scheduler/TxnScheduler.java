package com.assessment.miniwallet.scheduler;

import com.assessment.miniwallet.service.TxnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TxnScheduler {

    private final TxnService txnService;

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Kuala_Lumpur")
    public void scheduleDailyTransactionArchiving() {
        log.info("Starting daily transaction archiving job...");

        try {
            txnService.archiveCompletedTransactions();
            log.info("Successfully completed transaction archiving job.");
        } catch (Exception e) {
            log.error("Failed to archive transactions during scheduled job!", e);
        }
    }
}