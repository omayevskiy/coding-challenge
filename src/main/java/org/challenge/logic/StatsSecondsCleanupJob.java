package org.challenge.logic;

import org.challenge.model.SalesAmountPerSecond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class StatsSecondsCleanupJob {

    private final Clock clock;
    private final StatsService statsService;

    @Autowired
    public StatsSecondsCleanupJob(Clock clock, StatsService statsService) {
        this.clock = clock;
        this.statsService = statsService;
    }

    /**
     * removes aggregated sales amounts older than one minute
     */
    @Scheduled(fixedDelay = 123)
    public void cleanup() {
        SalesAmountPerSecond salesAmountPerSecond = statsService.statsQueue().peekFirst();
        LocalDateTime now = LocalDateTime.now(clock);
        while (salesAmountPerSecond != null
                && salesAmountPerSecond.getTime().isBefore(now.minus(60, ChronoUnit.SECONDS))) {
            salesAmountPerSecond = statsService.statsQueue().pollFirst();
        }
    }
}
