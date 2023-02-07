package com.thullo.data.repository;

import com.thullo.data.model.Token;
import com.thullo.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndTokenType(String verificationCode, String tokenType);
    @Modifying
    @Query(nativeQuery = true, value = "delete from token t where CURRENT_TIMESTAMP > t.expiry_date")
    void deleteExpiredToken();
    Optional<Token> findByUser(User user);
}
