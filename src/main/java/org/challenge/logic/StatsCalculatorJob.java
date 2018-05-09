package org.challenge.logic;

import org.challenge.model.SalesAmountPerSecond;
import org.challenge.model.SalesAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class StatsCalculatorJob {
    private final StatsService statsService;
    private final Clock clock;

    @Autowired
    public StatsCalculatorJob(StatsService statsService, Clock clock) {
        this.statsService = statsService;
        this.clock = clock;
    }

    /**
     * pools sales amounts from the sales queue, re-calculates aggregated statistics
     */
    @Scheduled(fixedDelay = 98)
    public void cacheStats() {
        SalesAmount salesAmount = statsService.retrieveNextSalesAmount();
        LocalDateTime now = LocalDateTime.now(clock).withNano(0);
        while (salesAmount != null && !salesAmount.getTime().isAfter(Instant.now(clock))) {
            /*
              Because sales requests are processed sequentially, this way we don't need to care about increasing the
              aggregated sales amount in parallel and use Atomic data structures.
             */
            SalesAmountPerSecond salesAmountPerSecond = statsService.getLastSalesAmountPerSecond();
            if (null != salesAmountPerSecond && salesAmountPerSecond.getTime().equals(now)) {
                salesAmountPerSecond.addSalesAmount(salesAmount.getAmount());
            } else {
                statsService.addSalesAmountPerSecond(new SalesAmountPerSecond(
                        salesAmount.getAmount(),
                        1,
                        now));
            }
            salesAmount = statsService.retrieveNextSalesAmount();
        }
    }
}
