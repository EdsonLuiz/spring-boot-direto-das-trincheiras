package com.edson.repository;

import com.edson.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserHardCodedRepository {
    private final UserData userData;

    public List<User> findAll() {
        return userData.getUsers();
    }

    public List<User> findAllByName(String firstName) {
        return userData.getUsers().stream()
                .filter(u -> u.getFirstName().equalsIgnoreCase(firstName))
                .toList();
    }

    public Optional<User> findById(Long id) {
        return userData.getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public User create(User user) {
        userData.getUsers().add(user);
        return user;
    }

    public void deleteById(Long id) {
        userData.getUsers().removeIf(u -> u.getId().equals(id));
    }

    public void update(User user) {
        if (findById(user.getId()).isPresent()) {
            deleteById(user.getId());
            create(user);
        }
    }
}
