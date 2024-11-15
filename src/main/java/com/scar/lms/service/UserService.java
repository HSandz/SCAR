package com.scar.lms.service;

import com.scar.lms.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    List<User> searchUsers(String keyword);

    User findUserById(int id);

    User findUsersByUsername(String username);

    User findUserByEmail(String email);

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    void addFavouriteFor(User user, int bookId);
}
