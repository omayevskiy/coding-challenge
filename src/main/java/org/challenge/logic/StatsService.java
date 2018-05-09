package org.challenge.logic;

import org.challenge.model.SalesAmountPerSecond;
import org.challenge.model.SalesAmount;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class StatsService {

    private final Deque<SalesAmountPerSecond> statsQueue = new ConcurrentLinkedDeque<>();
    private final Deque<SalesAmount> salesQueue = new ConcurrentLinkedDeque<>();

    public Deque<SalesAmount> salesQueue() {
        return salesQueue;
    }

    public Deque<SalesAmountPerSecond> statsQueue() {
        return statsQueue;
    }
}
