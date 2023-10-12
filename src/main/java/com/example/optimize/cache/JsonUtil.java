package com.example.optimize.cache;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * @author yangjh11
 * @date 2021/7/27
 */
@Slf4j
public class JsonUtil {


    public static <T> T readValue(String jsonStr, Class<T> valueType) {
        if (jsonStr == null || jsonStr.length() <= 0) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(jsonStr, valueType);
        } catch (JsonParseException | JsonMappingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("io exception : {}", e.getMessage());
        }
        return null;
    }

    public static String toJsonString(Object obj) {
        if (Objects.isNull(obj)) {
            return Strings.EMPTY;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("to json error:{}", e.getMessage(), e);
        }
        return Strings.EMPTY;
    }
}
