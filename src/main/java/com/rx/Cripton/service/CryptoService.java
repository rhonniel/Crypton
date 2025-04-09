package com.rx.Cripton.service;

import com.rx.Cripton.model.Coin;
import com.rx.Cripton.model.CoinPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CryptoService {


    private final WebClient webClient;

    @Autowired
    public CryptoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<CoinPrice> getCoinPriceStream( String id) {
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(3))
                .flatMap(i -> getCoinPrice(id))
                .doOnNext(price -> log.debug("coin price: {}", price.getPrice()))
                .onErrorContinue((error, obj) -> {
                    log.error("Error fetching coin price: {}", error.getMessage(), error);
                });
    }

    @SuppressWarnings("unchecked")
    private Flux<CoinPrice> getCoinPrice( String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quotes/latest")
                        .queryParam("id", Integer.parseInt(id))
                        .queryParam("convert", "USD")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    CoinPrice coinPrice = new CoinPrice();

                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    Map<String, Object> coin = (Map<String, Object>) data.get(id);
                    Map<String, Object> quote = (Map<String, Object>) coin.get("quote");
                    Map<String, Object> usd = (Map<String, Object>) quote.get("USD");

                    coinPrice.setSymbol(coin.get("symbol").toString());
                    coinPrice.setPrice(new BigDecimal(usd.get("price").toString()));
                    coinPrice.setPercentChange24h(new BigDecimal(usd.get("percent_change_24h").toString()));
                    coinPrice.setMarketCap(new BigDecimal(usd.get("market_cap").toString()));
                    coinPrice.setVolume24h(new BigDecimal(usd.get("volume_24h").toString()));

                    String lastUpdated = usd.get("last_updated").toString();
                    coinPrice.setLastUpdated(LocalDateTime.parse(lastUpdated.replace("Z", "")));

                    return coinPrice;
                })
                .flux()
                .onErrorResume(error -> {
                    log.error("Error calling CoinMarketCap API: {}", error.getMessage());
                    // Crear un objeto con datos de ejemplo cuando hay un error
                    CoinPrice fallbackPrice = new CoinPrice( "Error",
                            new BigDecimal("70000.00"),
                            new BigDecimal("7.7"),
                            new BigDecimal("770000000000"),
                            new BigDecimal("77000000000"),
                            LocalDateTime.now()
                    );
                    return Flux.just(fallbackPrice);
                });
    }


    public Mono<Coin> getCoins(String symbol){
        return webClient.get()
                .uri( uriBuilder -> uriBuilder
                        .path("/map")
                        .queryParam("symbol",symbol)
                        .build()).retrieve().bodyToMono(Map.class)
                .map( response -> {
                    Coin coin = new Coin();
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
                    Map<String, Object> coinData = dataList.get(0);

                    coin.setId(coinData.get("id").toString());
                    coin.setName(coinData.get("name").toString());
                    coin.setSymbol(coinData.get("symbol").toString());

                    return coin;
                });
    }

}
