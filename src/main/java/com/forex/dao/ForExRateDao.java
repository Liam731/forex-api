package com.forex.dao;

import com.forex.model.ForExRate;

import java.util.List;

public interface ForExRateDao {
    List<ForExRate> findByDateBetween(String startDate, String endDate);
}