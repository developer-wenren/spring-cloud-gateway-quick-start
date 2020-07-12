package com.one.learn.demo.userservice.domain;

import java.util.Random;
import java.util.UUID;

/**
 * @author one
 * @date 2020/07/12
 */
public class User {
    private Long id;
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(Long id, String name) {
        this.id = id;
        this.token = name;
    }

    public static User mock() {
        return new User(new Random().nextLong(), UUID.randomUUID().toString());
    }
}
