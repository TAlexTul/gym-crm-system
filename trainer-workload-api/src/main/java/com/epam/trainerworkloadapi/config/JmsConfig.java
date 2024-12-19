package com.epam.trainerworkloadapi.config;

import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setTypeIdPropertyName("content-type");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("providedTrainingSaveRequest", ProvidedTrainingSaveRequest.class);
        typeIdMappings.put("providedTrainingDeleteRequest", ProvidedTrainingDeleteRequest.class);
        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }
}
