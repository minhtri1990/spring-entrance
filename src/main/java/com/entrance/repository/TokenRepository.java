package com.entrance.repository;

import com.entrance.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Transactional
    @Modifying
    long deleteByUserIdEquals(Integer userId);

    Token findByUserIdEquals(Integer userId);

    Token findByRefreshTokenEquals(String refreshToken);
}
