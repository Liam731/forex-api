package com.forex.controller;

import com.forex.dto.ForExRateRequest;
import com.forex.dto.ForExRateResponse;
import com.forex.service.ForExRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forex")
public class ForExRateController {

    @Autowired
    private ForExRateService forExRateService;

    @PostMapping("/rates")
    public ResponseEntity<ForExRateResponse> getForExRatesByDateRange(@RequestBody ForExRateRequest request) {
        try {
            ForExRateResponse rates = forExRateService.getForExRatesByDateRange(request);
            return new ResponseEntity<>(rates, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}