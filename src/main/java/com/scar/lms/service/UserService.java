package com.scar.lms.service;

import com.scar.lms.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User findUsersByUsername(String username);

    List<User> searchUsers(String keyword);

    User findUserByEmail(String email);
}
