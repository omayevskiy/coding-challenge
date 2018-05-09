package org.challenge.logic;

import org.challenge.model.SalesAmountPerSecond;
import org.challenge.model.SalesStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FetchStatsUseCase {

    private final StatsService statsService;

    @Autowired
    public FetchStatsUseCase(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * reads aggregated sales amounts from last minute and calculates sales statistics.
     *
     * @return sales statistics with total sales amount and average amount per order
     */
    public SalesStatistics fetchStatsOfLastMinute() {
        List<SalesAmountPerSecond> sales = statsService.getAggregatedSalesAmountsFromLastMinute();
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
