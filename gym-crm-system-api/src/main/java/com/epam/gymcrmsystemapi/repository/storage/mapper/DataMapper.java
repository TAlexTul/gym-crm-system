package com.epam.gymcrmsystemapi.repository.storage.mapper;

public interface DataMapper<T> {

    T map(String[] data);

}
