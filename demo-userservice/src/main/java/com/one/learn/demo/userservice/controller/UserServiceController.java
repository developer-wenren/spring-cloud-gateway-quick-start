package com.one.learn.demo.userservice.controller;

import com.one.learn.demo.userservice.domain.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author one
 * @date 2020/07/12
 */
@RestController
@RequestMapping("/user")
public class UserServiceController {
    @RequestMapping("/get")
    public User get() {
        return User.mock();
    }
}
