package com.epam.gymcrmsystemapi.repository.storage.reader;

import com.epam.gymcrmsystemapi.repository.storage.mapper.DataMapper;

import java.util.Queue;

public interface DataReader<T> {

    Queue<T> read(String filePath, DataMapper<T> mapper);

}
