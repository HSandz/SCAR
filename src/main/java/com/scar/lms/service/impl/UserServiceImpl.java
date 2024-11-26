package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Role;
import com.scar.lms.entity.User;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.exception.UserNotFoundException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.UserService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserServiceImpl(final UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<User>> findAllUsers() {
        return CompletableFuture.supplyAsync(userRepository::findAll);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<User> findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.failedFuture(
                        new UserNotFoundException("User with username not found: " + username)));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<User> findUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.failedFuture(
                        new UserNotFoundException("User with email not found: " + email)));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<User>> searchUsers(String keyword) {
        return CompletableFuture.supplyAsync(() -> userRepository.searchUsers(keyword));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<User> findUserById(int id) {
        return userRepository
                .findById(id)
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.failedFuture(
                        new UserNotFoundException("User with ID not found: " + id)));
    }

    @Async
    @Override
    public void createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("User with username " + user.getUsername() + " already exists");
        } else if (userRepository.existsById(user.getId())) {
            throw new DuplicateResourceException("User with ID " + user.getId() + " already exists");
        }
        System.out.println("Saving user: " + user);
        userRepository.saveAndFlush(user);
        System.out.println("User saved.");
    }

    @Async
    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Async
    @Override
    public void deleteUser(int id) {
        var user = userRepository
                .findById(id)
                .orElseThrow(() -> new OperationNotAllowedException("Cannot delete user with id not found: " + id));
        userRepository.delete(user);
    }

    @Async
    @Override
    public void addFavouriteFor(User user, int bookId) {
        User persistedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new OperationNotAllowedException("Cannot add favourite for user not found: " + user.getUsername()));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new OperationNotAllowedException("Book not found with ID: " + bookId));

        if (persistedUser.getFavouriteBooks().contains(book)) {
            throw new OperationNotAllowedException("Book is already in the user's favorites");
        }

        persistedUser.getFavouriteBooks().add(book);

        userRepository.save(persistedUser);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> findFavouriteBooks(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return CompletableFuture.supplyAsync(() -> List.copyOf(user.getFavouriteBooks()));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public void removeFavouriteFor(User user, int bookId) {
        User persistedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new OperationNotAllowedException("Cannot remove favourite for user not found: " + user.getUsername()));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new OperationNotAllowedException("Book not found with ID: " + bookId));

        if (!persistedUser.getFavouriteBooks().contains(book)) {
            throw new OperationNotAllowedException("Book not found in user's favorites");
        }

        persistedUser.getFavouriteBooks().remove(book);

        userRepository.save(persistedUser);
    }

    @Async
    @Override
    public CompletableFuture<List<User>> findUsersByRole(Role role) {
        return CompletableFuture.supplyAsync(() -> userRepository.findByRole(role));
    }

    @Async
    @Override
    public CompletableFuture<Long> countAllUsers() {
        return CompletableFuture.supplyAsync(userRepository::count);
    }

    @Async
    @Override
    public CompletableFuture<Long> countUsersByRole(Role role) {
        return CompletableFuture.supplyAsync(() -> userRepository.countByRole(role));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Long> getFavouriteCount(int userId) {
        return CompletableFuture.supplyAsync(() -> userRepository.countFavouritesByUserId(userId));
    }
}
