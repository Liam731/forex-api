package com.forex.service;

import com.forex.dao.ForExRateDao;
import com.forex.dto.ForExRateRequest;
import com.forex.dto.ForExRateResponse;
import com.forex.model.ForExRate;
import com.forex.repository.ForExRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ForExRateServiceTest {

    @Mock
    private ForExRateRepository forExRateRepository;

    @Mock
    private ForExRateDao forExRateDao;

    @InjectMocks
    private ForExRateService forExRateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetForExRatesByDateRange_Success() {

        ForExRateRequest request = new ForExRateRequest();
        request.setStartDate("2024/09/30");
        request.setEndDate("2024/10/01");
        request.setCurrency("usd");

        // Prepare mocked data
        ForExRate rate1 = new ForExRate();
        rate1.setDate("20240930");
        rate1.setUsd("31.651");

        ForExRate rate2 = new ForExRate();
        rate2.setDate("20241001");
        rate2.setUsd("31.837");

        List<ForExRate> mockRates = Arrays.asList(rate1, rate2);

        // Mock the repository method to return the mocked data
        when(forExRateDao.findByDateBetween("20240930", "20241001")).thenReturn(mockRates);

        // Execute the method
        ForExRateResponse response = forExRateService.getForExRatesByDateRange(request);

        // Capture and verify the response data
        assertNotNull(response);
        assertEquals("0000", response.getError().getCode()); // Check success code
        assertEquals("Success", response.getError().getMessage());

        // Verify the date and rate fields in the response
        assertEquals("20240930", response.getCurrency().get(0).getDate());
        assertEquals("31.651", response.getCurrency().get(0).getUsd());
        assertEquals("20241001", response.getCurrency().get(1).getDate());
        assertEquals("31.837", response.getCurrency().get(1).getUsd());
    }

    @Test
    public void testGetForExRatesByDateRange_InvalidDateRange() {
        // Mock the request with invalid date range
        ForExRateRequest request = new ForExRateRequest();
        request.setStartDate("2023/09/30");
        request.setEndDate("2024/10/01");
        request.setCurrency("usd");

        // Execute the method
        ForExRateResponse response = forExRateService.getForExRatesByDateRange(request);

        // Validate the error response
        assertNotNull(response);
        assertEquals("E001", response.getError().getCode()); // Invalid date range code
        assertEquals("Invalid date range", response.getError().getMessage());
        assertNull(response.getCurrency()); // No currency list returned for invalid range
    }

    @Test
    public void testFetchDailyExchangeRates() throws Exception {

        // Simulate repository not having the date record
        when(forExRateRepository.findByDate(anyString())).thenReturn(Optional.empty());

        // Execute the method under test
        forExRateService.fetchDailyExchangeRates();

        // Capture and verify the saved ForExRate object
        ArgumentCaptor<ForExRate> forExRateCaptor = ArgumentCaptor.forClass(ForExRate.class);
        verify(forExRateRepository, atLeastOnce()).save(forExRateCaptor.capture());

        // Get the captured ForExRate object
        ForExRate capturedForExRate = forExRateCaptor.getValue();

        // Verify the format of the date and USD/NTD
        assertTrue(capturedForExRate.getDate().matches("\\d{8}"));  // Check if the date is in yyyyMMdd format
        assertTrue(capturedForExRate.getUsd().matches("\\d+\\.\\d{1,3}"));  // Check if USD/NTD is a number with up to two decimal places
    }
}