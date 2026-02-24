package com.edson.service;

import com.edson.domain.User;
import com.edson.exception.NotFoundException;
import com.edson.repository.UserHardCodedRepository;
import com.edson.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public List<User> findAll(String firstName) {
        if (firstName == null) {
            return repository.findAll();
        }
        return repository.findByFirstNameIgnoreCase(firstName);
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public User create(User user) {
        assertEmailDoesNotExist(user.getEmail());
        return repository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        this.asserUserExists(id);
        repository.deleteById(id);
    }

    @Transactional
    public void update(User user) {
        this.findById(user.getId());
        this.assertEmailDoesNotExist(user.getEmail(), user.getId());
        repository.save(user);
    }

    public void asserUserExists(Long id) {
        this.findById(id);
    }

    public void assertEmailDoesNotExist(String email) {
        repository.findByEmailIgnoreCase(email).ifPresent(UserService::throwEmailExistsException);
    }

    public void assertEmailDoesNotExist(String email, Long id) {
        repository.findByEmailIgnoreCase(email).ifPresent(UserService::throwEmailExistsException);
    }

    private static void throwEmailExistsException(User user) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email %s already exists".formatted(user.getEmail()));
    }
}
