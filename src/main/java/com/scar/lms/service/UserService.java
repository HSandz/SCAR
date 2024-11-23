package com.scar.lms.service;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Role;
import com.scar.lms.entity.User;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    CompletableFuture<List<User>> findAllUsers();

    List<User> searchUsers(String keyword);

    User findUserById(int id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    void addFavouriteFor(User user, int bookId);

    Set<Book> findFavouriteBooks(int id);

    void removeFavouriteFor(User user, int bookId);

    CompletableFuture<List<User>> findUsersByRole(Role role);

    CompletableFuture<Long> countAllUsers();

    CompletableFuture<Long> countUsersByRole(Role role);
}
