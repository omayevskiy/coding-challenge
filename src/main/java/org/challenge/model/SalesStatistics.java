package org.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SalesStatistics {
    private BigDecimal totalAmount; // 123.00
    private BigDecimal averageAmountPerOrder; // 45.04
}
