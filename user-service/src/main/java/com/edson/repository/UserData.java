package com.edson.repository;

import com.edson.domain.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
public class UserData {
    private final AtomicLong idGenerator = new AtomicLong();
    @Getter
    private final List<User> users = new ArrayList<>();

    public UserData() {
        users.add(User.builder().id(this.nextId()).firstName("firstName01").lastName("lastName01").email("firstName01@lastName01.com").build());
        users.add(User.builder().id(this.nextId()).firstName("firstName02").lastName("lastName02").email("firstName02@lastName02.com").build());
    }

    public Long nextId() {
        return idGenerator.getAndIncrement();
    }
}
