package com.example.optimize.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class GuavaCacheConfig {

    @Resource
    private MongoTemplate mongoTemplate;

    private final String CACHE = "cache";

    @Bean
    public LoadingCache<String, Object> guavaCache() {
        return CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Object load(String key) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("key").is(key));
                    return mongoTemplate.findOne(query, Object.class, CACHE);
                }
            });
    }
}