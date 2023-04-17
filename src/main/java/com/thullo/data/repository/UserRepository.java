package com.thullo.data.repository;

import com.thullo.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email IN :emails")
    List<User> findAllByEmails(@Param("emails") Set<String> emails);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:params% OR u.email LIKE %:params%")
    List<User> findByParams(@Param("params") String params);

    Optional<User> findByEmail(String email);

    User findUserByEmail(String email);

    Boolean existsByEmail(String email);

}
