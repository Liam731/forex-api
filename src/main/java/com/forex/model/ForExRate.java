package com.forex.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("ForExRates")
public class ForExRate {

    @Indexed(unique = true)
    private String date;
    private String usd;
}
