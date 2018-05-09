package org.challenge.logic;

import org.challenge.model.SalesAmountPerSecond;
import org.challenge.model.SalesStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class FetchStatsUseCase {

    private final Clock clock;
    private final StatsService statsService;

    @Autowired
    public FetchStatsUseCase(Clock clock, StatsService statsService) {
        this.clock = clock;
        this.statsService = statsService;
    }

    /**
     * reads aggregated sales amounts from last minute and calculates sales statistics.
     * @return sales statistics with total sales amount and average amount per order
     */
    public SalesStatistics fetchStatsOfLastMinute() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<SalesAmountPerSecond> sales = statsService.statsQueue().parallelStream()
                .filter(s -> s.getTime().isAfter(now.minus(60, ChronoUnit.SECONDS)))
                .collect(toList());
        if (sales.isEmpty()) {
            return new SalesStatistics(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal totalAmount = sales.parallelStream()
                .map(SalesAmountPerSecond::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Integer totalCount = sales.parallelStream()
                .map(SalesAmountPerSecond::getCount)
                .reduce(0, (x, y) -> x + y);
        BigDecimal avg = BigDecimal.valueOf(totalAmount.doubleValue() / totalCount);
        return new SalesStatistics(totalAmount, avg);
    }
}
