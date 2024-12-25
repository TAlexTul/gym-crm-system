package com.epam.trainerworkloadapi.config.mongodb;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class YearToIntegerConverter implements Converter<Year, Integer> {

    @Override
    public Integer convert(Year source) {
        return source.getValue();
    }
}
