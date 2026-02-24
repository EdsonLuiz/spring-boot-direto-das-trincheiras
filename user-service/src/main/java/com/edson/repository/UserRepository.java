package com.edson.repository;

import com.edson.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByFirstNameIgnoreCase(String firstName);
    Optional<User> findByEmailIgnoreCase(String email);
}
