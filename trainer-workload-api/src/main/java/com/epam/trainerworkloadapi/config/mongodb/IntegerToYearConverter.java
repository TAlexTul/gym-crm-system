package com.epam.trainerworkloadapi.config.mongodb;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class IntegerToYearConverter implements Converter<Integer, Year> {

    @Override
    public Year convert(Integer source) {
        return Year.of(source);
    }
}
