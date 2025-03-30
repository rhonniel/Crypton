package com.rx.Cripton.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WebFluxClient {
    private final WebClient webClient;

    public WebFluxClient(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api").build();
    }

    // Llama al servicio Mono
    public Mono<String> fetchMessage() {
        return webClient.get()
                .uri("/hello")
                .retrieve()
                .bodyToMono(String.class);
    }

    // Llama al servicio Flux
    public Flux<String> fetchDictionary() {
        return webClient.get()
                .uri("/dictionary")
                .retrieve()
                .bodyToFlux(String.class);
    }
}
