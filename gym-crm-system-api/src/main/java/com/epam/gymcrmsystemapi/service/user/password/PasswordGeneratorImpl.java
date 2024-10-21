package com.epam.gymcrmsystemapi.service.user.password;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordGeneratorImpl implements PasswordGenerator {

    @Value("${password.length}")
    private int passwordLength;
    @Value("${password.characters}")
    private String passwordCharacters;

    @Override
    public String generateRandomPassword() {
        var random = new SecureRandom();
        var password = new StringBuilder();

        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(passwordCharacters.length());
            char randomChar = passwordCharacters.charAt(randomIndex);
            password.append(randomChar);
        }

        return password.toString();
    }
}
