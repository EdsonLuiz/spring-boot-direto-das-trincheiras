package com.edson.service;

import com.edson.domain.User;
import com.edson.exception.NotFoundException;
import com.edson.repository.UserHardCodedRepository;
import com.edson.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserHardCodedRepository repository;
    private final UserRepository userRepository;

    public List<User> findAll(String firstName) {
        if(firstName == null) {
            return userRepository.findAll();
        }
        return repository.findAllByName(firstName);
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User create(User user) {
        return repository.create(user);
    }

    public void deleteById(Long id) {
        this.findById(id);
        repository.deleteById(id);
    }

    public void update(User user) {
        this.findById(user.getId());
        repository.update(user);
    }
}
