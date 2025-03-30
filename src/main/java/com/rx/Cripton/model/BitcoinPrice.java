package com.rx.Cripton.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitcoinPrice {
    private BigDecimal price;
    private BigDecimal percentChange24h;
    private BigDecimal marketCap;
    private BigDecimal volume24h;
    private LocalDateTime lastUpdated;
    private String currency = "USD";
}