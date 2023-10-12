package com.example.optimize.cache;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheUtils {

    private static CacheUtils cacheUtils;


    private static MongoCollection<Document> collection;

    private final String CACHE = "cache";

    private static final String KEY = "key";

    private static final String VALUE = "value";


    public CacheUtils(MongoTemplate mongoTemplate) {
        CacheUtils.collection = mongoTemplate.getCollection(CACHE);
    }

    public static String getStr(String key) {
        FindIterable<Document> documents = collection.find(Filters.eq(KEY, key));
        for (Document document : documents) {
            return document.get(VALUE).toString();
        }
        return null;
    }

    public static boolean exist(String key) {
        long l = collection.countDocuments(Filters.eq(KEY, key));
        return l > 0;
    }

    public static void setStr(String key, Object value) {
        Document document = new Document(KEY, key).append(VALUE, value);
        if (!exist(key)) {
            collection.insertOne(document);
        }
    }

    public static <T> T getObject(String key, Class<T> cz) {
        FindIterable<Document> documents = collection.find(Filters.eq(KEY, key));
        for (Document document : documents) {
            String data = document.get(VALUE).toString();
            T t = JsonUtil.readValue(data, cz);
            return t;
        }
        return null;
    }


    public static void setObject(String key, Object value) {
        String v = JsonUtil.toJsonString(value);
        Document document = new Document(KEY, key).append(VALUE, v);
        if (exist(key)) {
            collection.insertOne(document);
        }
    }
    public static void setEmptyKey(String key) {
        setStr(key, StringUtils.EMPTY);
    }
    public static void delKey(String key) {
        collection.deleteOne(Filters.eq(KEY, key));
    }

}
