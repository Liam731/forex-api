package com.forex.dao.impl;

import com.forex.dao.ForExRateDao;
import com.forex.model.ForExRate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ForExRateDaoImpl implements ForExRateDao {
    private final MongoTemplate mongoTemplate;

    public ForExRateDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ForExRate> findByDateBetween(String startDate, String endDate) {

        Query query = new Query();
        query.addCriteria(Criteria.where("date").gte(startDate).lte(endDate));

        return mongoTemplate.find(query, ForExRate.class);
    }
}
