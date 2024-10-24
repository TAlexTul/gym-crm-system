package com.epam.gymcrmsystemapi.config.security.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminProperties {

    @NotNull(message = "first name must not be null")
    private String firstName;

    @NotNull(message = "last name must not be null")
    private String lastName;

    @NotEmpty(message = "password must not be empty")
    @Size(min = 10, message = "password's length must be at least 10")
    private char[] password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
