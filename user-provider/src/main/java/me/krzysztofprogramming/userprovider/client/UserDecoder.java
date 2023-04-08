package me.krzysztofprogramming.userprovider.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.jackson.JacksonDecoder;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Slf4j
class UserDecoder extends JacksonDecoder {
    public UserDecoder() {
        super(createCustomObjectMapper());
    }

    private static ObjectMapper createCustomObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        return objectMapper;
    }
}
