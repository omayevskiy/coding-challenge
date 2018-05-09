package org.challenge.logic;

import org.challenge.model.SalesAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

@Service
public class AcceptSalesUseCase {

    private final Clock clock;
    private final StatsService statsService;

    @Autowired
    public AcceptSalesUseCase(Clock clock, StatsService statsService) {
        this.clock = clock;
        this.statsService = statsService;
    }

    public void accept(String salesAmount) {
        statsService.salesQueue().add(new SalesAmount(new BigDecimal(salesAmount), Instant.now(clock)));
    }
}
