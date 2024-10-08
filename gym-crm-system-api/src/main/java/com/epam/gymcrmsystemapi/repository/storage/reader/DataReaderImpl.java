package com.epam.gymcrmsystemapi.repository.storage.reader;

import com.epam.gymcrmsystemapi.exceptions.IOExceptions;
import com.epam.gymcrmsystemapi.repository.storage.mapper.DataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

@Service
public class DataReaderImpl<T> implements DataReader<T> {

    private static final Logger log = LoggerFactory.getLogger(DataReaderImpl.class);

    @Override
    public Queue<T> read(String filePath, DataMapper<T> mapper) {
        Queue<T> queue = new ArrayDeque<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream(filePath),
                        "Resource not found: " + filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] data = line.split(",");
                queue.add(mapper.map(data));
            }
        } catch (IOException e) {
            log.error("Unchecked IOException", e);
            throw IOExceptions.uncheckedIOException(e);
        }
        return queue;
    }
}
