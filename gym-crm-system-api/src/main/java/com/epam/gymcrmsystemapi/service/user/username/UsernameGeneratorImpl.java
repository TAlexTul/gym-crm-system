package com.epam.gymcrmsystemapi.service.user.username;

import com.epam.gymcrmsystemapi.exceptions.UserExceptions;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UsernameGeneratorImpl implements UsernameGenerator {

    private final UserRepository userRepository;

    public UsernameGeneratorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String calculateUsername(String firstName, String lastName) {
        if (userRepository.existsByFirstNameAndLastName(firstName, lastName))
            throw UserExceptions.duplicateFirstNameAndLastName(firstName, lastName);

        return String.join(".",
                firstName.trim(),
                lastName.trim());
    }
}
