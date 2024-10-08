package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerMapper implements DataMapper<Trainer> {

    private DataMapper<User> mapper;

    @Autowired
    public void setMapper(DataMapper<User> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Trainer map(String[] data) {
        User user = mapper.map(data);
        Long id = Long.parseLong(data[6]);
        String specialization = data[7];

        var trainer = new Trainer();
        trainer.setId(id);
        trainer.setSpecialization(specialization);
        trainer.setUser(user);

        return trainer;
    }
}
