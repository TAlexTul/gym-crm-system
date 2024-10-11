package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class TraineeMapper implements DataMapper<Trainee> {

    private DataMapper<User> mapper;

    @Autowired
    public void setMapper(DataMapper<User> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Trainee map(String[] data) {
        User user = mapper.map(data);
        Long id = Long.parseLong(data[6]);
        OffsetDateTime dateOfBirth = OffsetDateTime.parse(data[7]);
        String address = data[8];

        var trainee = new Trainee();
        trainee.setId(id);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setUser(user);

        return trainee;
    }
}
