package com.serviceinvoker.reflectionhelper.server.service;

import com.serviceinvoker.reflectionhelper.server.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {

    public HashMap<String, User> users;

    @PostConstruct
    private void init(){
        this.users = new HashMap<>();
        this.users.put("John", new User("John", "Doe", 16));
        this.users.put("Jane", new User("Jane", "Doe", 18));
    }

    public  User getUser(String name) {
        return users.get(name);
    }
}
