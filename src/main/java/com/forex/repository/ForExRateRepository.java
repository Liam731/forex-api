package com.forex.repository;

import com.forex.model.ForExRate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ForExRateRepository extends MongoRepository<ForExRate, String> {
    Optional<ForExRate> findByDate(String date);
}