package com.forex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForExRateDTO {
    private String date;
    private String usd;
}
