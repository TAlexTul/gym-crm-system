package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select case when count (u) > 0 then true else false end " +
            "from User u where u.firstName = :firstName and u.lastName = :lastName")
    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    @Query("select max(t.id) from User t")
    Long selectMaxId();

    @Query("select case when count(u) > 0 then true else false end from User u where u.username = :username")
    boolean existsByUsername(String username);

}
