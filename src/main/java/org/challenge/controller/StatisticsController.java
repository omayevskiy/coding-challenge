package org.challenge.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.challenge.logic.FetchStatsUseCase;
import org.challenge.model.SalesStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
public class StatisticsController {
    private final FetchStatsUseCase fetchStatsUseCase;

    @Autowired
    StatisticsController(FetchStatsUseCase fetchStatsUseCase) {
        this.fetchStatsUseCase = fetchStatsUseCase;
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    StatsDTO fetchStats() {
        return StatsDTO.from(fetchStatsUseCase.fetchStatsOfLastMinute());
    }


    @Data
    @Builder
    static class StatsDTO {

        @JsonProperty("total_sales_amount")
        private String totalSalesAmount; // "10.00"

        @JsonProperty("average_amount_per_order")
        private String averageAmountPerOrder; // "45.04"

        static StatsDTO from(SalesStatistics salesStatistics) {
            return StatsDTO.builder()
                    .totalSalesAmount(formatWithTwoDigitsAfterComma(salesStatistics.getTotalAmount()))
                    .averageAmountPerOrder(formatWithTwoDigitsAfterComma(salesStatistics.getAverageAmountPerOrder()))
                    .build();
        }

        static String formatWithTwoDigitsAfterComma(BigDecimal totalSalesAmount) {
            return totalSalesAmount.setScale(2, RoundingMode.HALF_UP).toString();
        }
    }
}

