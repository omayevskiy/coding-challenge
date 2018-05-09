package org.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class SalesAmount {
    private BigDecimal amount; // 10.00
    private Instant time; // 2017-01-16T12:34:00
}
