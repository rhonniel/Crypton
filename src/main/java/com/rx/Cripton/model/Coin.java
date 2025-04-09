package com.rx.Cripton.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coin {
    private String id;
    private String name;
    private String symbol;
}
