package com.rx.Cripton.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/hello")
    public Mono<String> hello(){
        return Mono.just("Hello RX, World!");
    }

    @GetMapping("/dictionary")
    public Flux<String> getDictionary(){
        List<String> words = List.of("Pacha", " ama", " mucho", " a", " pachaaaaaaaaaaaaaaaa");

        return Flux.fromIterable(words)
                .delayElements(Duration.ofSeconds(2)); // Envía una palabra por segundo
    }
}
