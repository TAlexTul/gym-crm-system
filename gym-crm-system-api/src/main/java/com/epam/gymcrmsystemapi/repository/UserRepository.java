package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select case when count (u) > 0 then true else false end " +
            "from User u where u.firstName = :firstName and u.lastName = :lastName")
    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    Optional<User> findByUsername(String username);

    @Query("update User u set u.status = :status where u.username = :username")
    @Modifying
    void changeStatusByEmail(String username, UserStatus status);

}
