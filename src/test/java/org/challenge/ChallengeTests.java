package org.challenge;

import org.challenge.logic.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DisplayName("sales statistics tests")
class ChallengeTests {

    @Autowired
    StatsService statsService;

    @BeforeEach
    void init() {
        statsService.statsQueue().clear();
    }

    @Test
    @DisplayName("shows zero statistic, if no sales were posted yet")
    void showsEmptyStatistics(@Autowired WebTestClient webTestClient) {
        webTestClient.get()
                .uri("/statistics")
                .exchange()
                .expectBody()
                .jsonPath("$.total_sales_amount").isEqualTo("0.00")
                .jsonPath("$.average_amount_per_order").isEqualTo("0.00");
    }

    @Test
    @DisplayName("accepts new sales amount")
    void acceptsSalesAmount(@Autowired WebTestClient webTestClient) {
        webTestClient.post()
                .uri("/sales?sales_amount=10.0").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("generates statistics for last minute, if posting sales in parallel")
    void generatesStatics(@Autowired WebTestClient webTestClient) throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            tasks.add(() -> {
                webTestClient.post()
                        .uri("/sales?sales_amount=12.0").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .exchange();
                return null;
            });
            tasks.add(() -> {
                webTestClient.post()
                        .uri("/sales?sales_amount=11.0").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .exchange();
                return null;
            });
            tasks.add(() -> {
                webTestClient.post()
                        .uri("/sales?sales_amount=1.0").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .exchange();
                return null;
            });
        }
        es.invokeAll(tasks);
        await().atMost(5, SECONDS).untilAsserted(() -> webTestClient.get()
                .uri("/statistics")
                .exchange()
                .expectBody()
                .jsonPath("total_sales_amount").isEqualTo("1200.00")
                .jsonPath("average_amount_per_order").isEqualTo("8.00"));
    }
}