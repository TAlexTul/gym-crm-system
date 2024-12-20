package com.epam.trainerworkloadapi.config.mongodb;

import org.junit.jupiter.api.Test;

import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class MonthToIntegerConverterTest {

    private final MonthToIntegerConverter converter = new MonthToIntegerConverter();

    @Test
    void testConvert() {
        assertThat(converter.convert(Month.JANUARY)).isEqualTo(1);
        assertThat(converter.convert(Month.FEBRUARY)).isEqualTo(2);
        assertThat(converter.convert(Month.MARCH)).isEqualTo(3);
        assertThat(converter.convert(Month.APRIL)).isEqualTo(4);
        assertThat(converter.convert(Month.MAY)).isEqualTo(5);
        assertThat(converter.convert(Month.JUNE)).isEqualTo(6);
        assertThat(converter.convert(Month.JULY)).isEqualTo(7);
        assertThat(converter.convert(Month.AUGUST)).isEqualTo(8);
        assertThat(converter.convert(Month.SEPTEMBER)).isEqualTo(9);
        assertThat(converter.convert(Month.OCTOBER)).isEqualTo(10);
        assertThat(converter.convert(Month.NOVEMBER)).isEqualTo(11);
        assertThat(converter.convert(Month.DECEMBER)).isEqualTo(12);
    }
}
