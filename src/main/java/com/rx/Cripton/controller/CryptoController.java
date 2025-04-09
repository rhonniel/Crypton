package com.rx.Cripton.controller;

import com.rx.Cripton.model.Coin;
import com.rx.Cripton.model.CoinPrice;
import com.rx.Cripton.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class CryptoController {

    private final CryptoService cryptoService;

    @Autowired
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/")
    public Mono<String> index(Model model) {

        return Mono.just("index");
    }

    @GetMapping(value = "/api/coin/price/{coinId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<CoinPrice> getCoinPriceStream(@PathVariable String coinId ) {
        return cryptoService.getCoinPriceStream(coinId);
    }

    @GetMapping(value = "/api/coin/{symbol}")
    public Mono<Coin> getCoin(@PathVariable("symbol") String symbol){
        return cryptoService.getCoins(symbol);
    }

}
