package com.entrance.repository;

import com.entrance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailEquals(String email);

    Optional<User> findByIdEquals(Integer id);
}
