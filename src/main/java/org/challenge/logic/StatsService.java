package org.challenge.logic;

import org.challenge.model.SalesAmountPerSecond;
import org.challenge.model.SalesAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.util.stream.Collectors.toList;

@Service
public class StatsService {

    public final Deque<SalesAmountPerSecond> statsQueue = new ConcurrentLinkedDeque<>();
    private final Deque<SalesAmount> salesQueue = new ConcurrentLinkedDeque<>();
    private final Clock clock;

    @Autowired
    public StatsService(Clock clock) {
        this.clock = clock;
    }

    void addSalesAmount(SalesAmount salesAmount) {
        salesQueue.add(salesAmount);
    }

    SalesAmount retrieveNextSalesAmount() {
        return salesQueue.pollFirst();
    }

    List<SalesAmountPerSecond> getAggregatedSalesAmountsFromLastMinute() {
        return statsQueue.parallelStream()
                .filter(s -> s.getTime()
                        .isAfter(LocalDateTime.now(clock).minus(60, ChronoUnit.SECONDS)))
                .collect(toList());
    }

    SalesAmountPerSecond getLastSalesAmountPerSecond() {
        return statsQueue.peekLast();
    }

    void addSalesAmountPerSecond(SalesAmountPerSecond salesAmountPerSecond) {
        statsQueue.add(salesAmountPerSecond);
    }

    SalesAmountPerSecond getAndRemoveLastSalesAmountPerSecond() {
        return statsQueue.pollFirst();
    }
}
