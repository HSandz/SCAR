package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Role;
import com.scar.lms.entity.User;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.UserService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserServiceImpl(final UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<User>> findAllUsers() {
        return CompletableFuture.completedFuture(userRepository.findAll());
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public User findUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email not found: " + email));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<User>> searchUsers(String keyword) {
        return CompletableFuture.supplyAsync(() -> userRepository.searchUsers(keyword));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public User findUserById(int id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id not found: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException("User with id not found: " + id));
        userRepository.delete(user);
    }

    @Async
    @Override
    public void addFavouriteFor(User user, int bookId) {
        User persistedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new OperationNotAllowedException("User not found: " + user.getUsername()));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new OperationNotAllowedException("Book not found with ID: " + bookId));

        if (persistedUser.getFavouriteBooks().contains(book)) {
            throw new OperationNotAllowedException("Book is already in the user's favorites");
        }

        persistedUser.getFavouriteBooks().add(book);

        userRepository.save(persistedUser);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> findFavouriteBooks(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return CompletableFuture.supplyAsync(() -> List.copyOf(user.getFavouriteBooks()));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public void removeFavouriteFor(User user, int bookId) {
        User persistedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new OperationNotAllowedException("User not found: " + user.getUsername()));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new OperationNotAllowedException("Book not found with ID: " + bookId));

        if (!persistedUser.getFavouriteBooks().contains(book)) {
            throw new OperationNotAllowedException("Book not found in user's favorites");
        }

        persistedUser.getFavouriteBooks().remove(book);

        userRepository.save(persistedUser);
    }

    @Override
    public CompletableFuture<List<User>> findUsersByRole(Role role) {
        return CompletableFuture.supplyAsync(() -> userRepository.findByRole(role));
    }

    @Override
    public CompletableFuture<Long> countAllUsers() {
        return CompletableFuture.supplyAsync(userRepository::count);
    }

    @Override
    public CompletableFuture<Long> countUsersByRole(Role role) {
        return CompletableFuture.supplyAsync(() -> userRepository.countByRole(role));
    }
}
