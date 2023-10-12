package com.example.optimize;

import com.example.optimize.cache.Book;
import com.example.optimize.cache.CacheUtils;
import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class OptimizeCwsApplicationTests {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void contextLoads() {
        List<Bson> bsonList = Lists.newArrayList();
        bsonList.add(Filters.eq("quoteId", "quoteId"));
        bsonList.add(Filters.eq("priceFlag", true));
        bsonList.add(Filters.eq("deleted", false));

        bsonList.add(Filters.eq("certificationFee", true));
        Bson bson = Filters.and(bsonList);
        System.out.println(mongoTemplate.getCollection("inventory").countDocuments(bson) > 0);

    }

    @Test
    void contextLoads1() {
        CacheUtils.setEmptyKey("test");

    }

}
