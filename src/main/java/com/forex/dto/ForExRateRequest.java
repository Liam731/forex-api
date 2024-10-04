package com.forex.dto;

import lombok.Data;

@Data
public class ForExRateRequest {
    private String startDate;
    private String endDate;
    private String currency;
}