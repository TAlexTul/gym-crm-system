package com.epam.gymcrmsystemapi.service.user.username;

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
        boolean isExists = userRepository.existsByFirstNameAndLastName(firstName, lastName);
        if (isExists) {
            Long suffix = userRepository.selectMaxId();
            return String.join(".",
                    firstName.trim(),
                    lastName.trim(),
                    (++suffix).toString());
        } else {
            return String.join(".",
                    firstName.trim(),
                    lastName.trim());
        }
    }
}
