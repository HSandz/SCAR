package com.scar.lms.service;

import com.scar.lms.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    List<User> findUsersByUsername(String username);

    User findUserByEmail(String email);
}
