package com.epam.gymcrmsystemapi.model.user;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "authorities")
public class UserAuthority {

    @Id
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.ORDINAL)
    private KnownAuthority id;

    @ManyToMany(mappedBy = "authorities")
    @SuppressWarnings("FieldMayBeFinal")
    private Set<User> users = new HashSet<>();

    public KnownAuthority getId() {
        return id;
    }

    public void setId(KnownAuthority id) {
        this.id = id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAuthority that = (UserAuthority) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
