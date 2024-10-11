package com.epam.gymcrmsystemapi.repository.storage.reader;

import com.epam.gymcrmsystemapi.repository.storage.mapper.DataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataReaderImplTest {

    private DataReaderImpl<String> dataReader;
    private DataMapper<String> mapper;

    @BeforeEach
    void setUp() {
        dataReader = new DataReaderImpl<>();
        mapper = Mockito.mock(DataMapper.class);
    }

    @Test
    @Disabled(value = "you must create file before test")
    void testRead() throws IOException {
        Path tempFile = Paths.get("src", "test", "resources", "test-file.csv");
        Files.writeString(tempFile, "1,John,Doe\n2,Jane,Doe\n# Commented line\n");

        when(mapper.map(new String[]{"1", "John", "Doe"})).thenReturn("John");
        when(mapper.map(new String[]{"2", "Jane", "Doe"})).thenReturn("Jane");

        Queue<String> result = dataReader.read("test-file.csv", mapper);

        assertEquals(2, result.size());
        assertTrue(result.contains("John"));
        assertTrue(result.contains("Jane"));

        verify(mapper).map(new String[]{"1", "John", "Doe"});
        verify(mapper).map(new String[]{"2", "Jane", "Doe"});

        Files.deleteIfExists(tempFile);
    }
}
