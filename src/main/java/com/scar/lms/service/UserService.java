package com.scar.lms.service;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Role;
import com.scar.lms.entity.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    CompletableFuture<List<User>> findAllUsers();

    CompletableFuture<List<User>> searchUsers(String keyword);

    CompletableFuture<User> findUserById(int id);

    CompletableFuture<User> findUserByUsername(String username);

    CompletableFuture<User> findUserByEmail(String email);

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    void addFavouriteFor(User user, int bookId);

    CompletableFuture<List<Book>> findFavouriteBooks(int id);

    void removeFavouriteFor(User user, int bookId);

    CompletableFuture<List<User>> findUsersByRole(Role role);

    CompletableFuture<Long> countAllUsers();

    CompletableFuture<Long> countUsersByRole(Role role);

    CompletableFuture<Long> getFavouriteCount(int userId);
}
