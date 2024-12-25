package com.epam.trainerworkloadapi.config.mongodb;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Month;

@Component
public class MonthToIntegerConverter implements Converter<Month, Integer> {

    @Override
    public Integer convert(Month source) {
        return source.getValue();
    }
}
