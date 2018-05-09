package org.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SalesAmountPerSecond {
    private BigDecimal totalAmount; // 10.00
    private int count; // 7
    private LocalDateTime time; // 2017-01-16T12:34:00

    public void addSalesAmount(BigDecimal salesAmount) {
        this.totalAmount = totalAmount.add(salesAmount);
        this.count++;
    }
}
