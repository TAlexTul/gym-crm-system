package com.epam.trainerworkloadapi.config.mongodb;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Month;

@Component
public class IntegerToMonthConverter implements Converter<Integer, Month> {

    @Override
    public Month convert(Integer source) {
        return Month.of(source);
    }
}
