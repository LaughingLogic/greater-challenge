package au.com.greater.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * This component is responsible for executing transaction processing jobs at
 * pre-determined time intervals.
 *
 * The scheduler makes the following assumptions:
 *
 * - The time zone is always UTC
 * - Transaction files take one minute or less to be received
 *
 * Based on the above, the scheduler runs at 06:01am and 21:01pm each day.
 *
 * @author Justin Lewis Salmon
 */
@Component
public class Scheduler {

  private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

  private final TransactionProcessor transactionProcessor;

  @Autowired
  public Scheduler(TransactionProcessor transactionProcessor) {
    this.transactionProcessor = transactionProcessor;
  }

  @Schedules({
      @Scheduled(cron = "0 1 6  * * ?"), // 06:01am
      @Scheduled(cron = "0 1 21 * * ?"), // 21:01pm
  })
  public void executeTransactionProcessor() {
    log.info("Performing scheduled transaction processing, time is {}",
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmX")
          .withZone(ZoneOffset.UTC)
          .format(Instant.now()));

    try {
      transactionProcessor.execute();
    } catch (RuntimeException e) {
      log.error("Error processing transactions: {}: {}", e.getMessage(), e.getCause());
    }
  }
}
