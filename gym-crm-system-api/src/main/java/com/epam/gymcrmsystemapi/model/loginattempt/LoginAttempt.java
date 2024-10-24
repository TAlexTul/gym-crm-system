package com.epam.gymcrmsystemapi.model.loginattempt;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "attempts")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(name = "last_modified", nullable = false)
    private OffsetDateTime lastModified = OffsetDateTime.now();

    public LoginAttempt() {
    }

    public LoginAttempt(String username, int attempts,
                        OffsetDateTime lastModified) {
        this.username = username;
        this.attempts = attempts;
        this.lastModified = lastModified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginAttempt that = (LoginAttempt) o;
        return attempts == that.attempts
                && Objects.equals(id, that.id)
                && Objects.equals(username, that.username)
                && Objects.equals(lastModified, that.lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, attempts, lastModified);
    }
}

