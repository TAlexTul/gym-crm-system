package com.epam.trainerworkloadapi.config.mongodb;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

class YearToIntegerConverterTest {

    private final YearToIntegerConverter converter = new YearToIntegerConverter();

    @Test
    void testConvert() {
        assertThat(converter.convert(Year.of(2024))).isEqualTo(2024);
        assertThat(converter.convert(Year.of(2000))).isEqualTo(2000);
        assertThat(converter.convert(Year.of(1990))).isEqualTo(1990);
        assertThat(converter.convert(Year.of(1500))).isEqualTo(1500);
    }
}
