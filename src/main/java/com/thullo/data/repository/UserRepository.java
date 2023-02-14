package com.thullo.data.repository;

import com.thullo.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.email IN :emails")
    List<User> findAllByEmails(@Param("emails") List<String> emails);

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
