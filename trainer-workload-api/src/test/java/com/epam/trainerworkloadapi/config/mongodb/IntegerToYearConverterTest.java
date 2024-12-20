package com.epam.trainerworkloadapi.config.mongodb;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerToYearConverterTest {

    private final IntegerToYearConverter converter = new IntegerToYearConverter();

    @Test
    void testConvert() {
        assertThat(converter.convert(2023)).isEqualTo(Year.of(2023));
        assertThat(converter.convert(1900)).isEqualTo(Year.of(1900));
        assertThat(converter.convert(0)).isEqualTo(Year.of(0)); // 0 - это год по стандарту
    }
}
