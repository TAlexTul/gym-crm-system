package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import com.epam.gymcrmsystemapi.model.user.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

public interface AuthorityRepository extends JpaRepository<UserAuthority, KnownAuthority> {

    Set<KnownAuthority> ADMIN_AUTHORITIES = EnumSet.of(KnownAuthority.ROLE_ADMIN);

    Stream<UserAuthority> findAllByIdIn(Set<KnownAuthority> ids);

}
