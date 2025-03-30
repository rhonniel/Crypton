package com.rx.Cripton.service;

import com.rx.Cripton.model.BitcoinPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class CryptoService {


    private final WebClient webClient;

    @Autowired
    public CryptoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<BitcoinPrice> getBitcoinPriceStream() {
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(3))
                .flatMap(i -> getBitcoinPrice())
                .doOnNext(price -> log.debug("Bitcoin price: {}", price.getPrice()))
                .onErrorContinue((error, obj) -> {
                    log.error("Error fetching Bitcoin price: {}", error.getMessage(), error);
                });
    }

    @SuppressWarnings("unchecked")
    private Flux<BitcoinPrice> getBitcoinPrice() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("id", "1") // Bitcoin ID in CoinMarketCap
                        .queryParam("convert", "USD")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    BitcoinPrice bitcoinPrice = new BitcoinPrice();

                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    Map<String, Object> bitcoin = (Map<String, Object>) data.get("1");
                    Map<String, Object> quote = (Map<String, Object>) bitcoin.get("quote");
                    Map<String, Object> usd = (Map<String, Object>) quote.get("USD");

                    bitcoinPrice.setPrice(new BigDecimal(usd.get("price").toString()));
                    bitcoinPrice.setPercentChange24h(new BigDecimal(usd.get("percent_change_24h").toString()));
                    bitcoinPrice.setMarketCap(new BigDecimal(usd.get("market_cap").toString()));
                    bitcoinPrice.setVolume24h(new BigDecimal(usd.get("volume_24h").toString()));

                    String lastUpdated = usd.get("last_updated").toString();
                    bitcoinPrice.setLastUpdated(LocalDateTime.parse(lastUpdated.replace("Z", "")));

                    return bitcoinPrice;
                })
                .flux()
                .onErrorResume(error -> {
                    log.error("Error calling CoinMarketCap API: {}", error.getMessage());
                    // Crear un objeto con datos de ejemplo cuando hay un error
                    BitcoinPrice fallbackPrice = new BitcoinPrice(
                            new BigDecimal("50000.00"),
                            new BigDecimal("2.5"),
                            new BigDecimal("950000000000"),
                            new BigDecimal("30000000000"),
                            LocalDateTime.now(),
                            "USD"
                    );
                    return Flux.just(fallbackPrice);
                });
    }

}
