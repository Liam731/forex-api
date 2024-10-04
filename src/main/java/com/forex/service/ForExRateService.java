package com.forex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.dao.ForExRateDao;
import com.forex.dto.ErrorDTO;
import com.forex.dto.ForExRateDTO;
import com.forex.dto.ForExRateRequest;
import com.forex.dto.ForExRateResponse;
import com.forex.model.ForExRate;
import com.forex.repository.ForExRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ForExRateService {

    @Autowired
    private ForExRateRepository forExRateRepository;

    @Autowired
    private ForExRateDao forExRateDao;

    @Value("${api.url}")
    private String apiUrl;

    private static final Logger log = LoggerFactory.getLogger(ForExRateService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // Get exchange rate records by date range
    public ForExRateResponse getForExRatesByDateRange(ForExRateRequest request) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate startDate = LocalDate.parse(request.getStartDate(), inputFormatter);
        LocalDate endDate = LocalDate.parse(request.getEndDate(), inputFormatter);

        // Validate the date range
        if (!isDateRangeValid(startDate, endDate)) {
            return new ForExRateResponse(new ErrorDTO("E001", "Invalid date range"));
        }

        // Query the database and convert the result to DTOs
        List<ForExRate> result = forExRateDao.findByDateBetween(startDate.format(outputFormatter), endDate.format(outputFormatter));

        List<ForExRateDTO> currencyList = new ArrayList<>();
        for (ForExRate rate : result) {
            ForExRateDTO currencyRate = new ForExRateDTO(rate.getDate(), rate.getUsd());
            currencyList.add(currencyRate);
        }

        // Construct a successful response
        return new ForExRateResponse(new ErrorDTO("0000", "Success"), currencyList);
    }

    // Validate the date range
    private boolean isDateRangeValid(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        return !startDate.isBefore(now.minusYears(1)) && !endDate.isAfter(now.minusDays(1));
    }

    // Scheduled task that runs daily at 18:00
    @Scheduled(cron = "0 0 18 * * ?")
    public void fetchDailyExchangeRates() {

        ObjectMapper objectMapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "Mozilla/5.0");

        // Create HttpEntity containing request headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Call the forex API and get the raw JSON response
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String jsonResponse = response.getBody();

            // Parse the JSON string into a list of maps
            List<Map<String, String>> ratesList = objectMapper.readValue(jsonResponse, new TypeReference<>() {
            });

            // Filter by Date and USD/NTD and store them in the collection
            for (Map<String, String> rate : ratesList) {
                String date = rate.get("Date");
                String usdNtd = rate.get("USD/NTD");

                // Check if the exchange rate record for the same date already exists
                Optional<ForExRate> existingRate = forExRateRepository.findByDate(date);
                if (existingRate.isPresent()) {
                    log.warn("Record for Date = {} already exists. Skipping insert.", date);
                    continue;
                }

                // Store the date and USD/NTD into MongoDB
                ForExRate forExRate = new ForExRate();
                forExRate.setDate(date);
                forExRate.setUsd(usdNtd);
                forExRateRepository.save(forExRate);

                log.info("Saved rate: Date = {}, USD/NTD = {}", date, usdNtd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
