package com.forex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForExRateResponse {
    private ErrorDTO error;
    private List<ForExRateDTO> currency;

    public ForExRateResponse(ErrorDTO error) {
        this.error = error;
    }
}
