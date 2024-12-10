package com.epam.gymcrmsystemapi.config;

import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingSaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import java.util.Collections;

@Configuration
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setTypeIdPropertyName("content-type");
        converter.setTypeIdMappings(
                Collections.singletonMap("providedTrainingSaveRequest", ProvidedTrainingSaveRequest.class));
        return converter;
    }
}
