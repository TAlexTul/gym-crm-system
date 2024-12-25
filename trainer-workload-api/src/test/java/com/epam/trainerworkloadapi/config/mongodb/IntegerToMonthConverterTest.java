package com.epam.trainerworkloadapi.config.mongodb;

import org.junit.jupiter.api.Test;

import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerToMonthConverterTest {

    private final IntegerToMonthConverter converter = new IntegerToMonthConverter();

    @Test
    void testConvert() {
        assertThat(converter.convert(1)).isEqualTo(Month.JANUARY);
        assertThat(converter.convert(2)).isEqualTo(Month.FEBRUARY);
        assertThat(converter.convert(3)).isEqualTo(Month.MARCH);
        assertThat(converter.convert(12)).isEqualTo(Month.DECEMBER);
    }
}
