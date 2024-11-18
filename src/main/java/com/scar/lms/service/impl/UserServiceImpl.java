package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
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
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public User findUsersByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("fuck you: " + username));
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
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
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
        if (!userRepository.existsById(user.getId())) {
            throw new OperationNotAllowedException("Unable to add favourite book for user " + user.getUsername());
        } else if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
            throw new OperationNotAllowedException("Unable to add favourite book for user " + user.getUsername());
        } else if (bookRepository.findById(bookId).isEmpty()) {
            throw new OperationNotAllowedException("Unable to add favourite book for user " + user.getUsername());
        }
        Set<Book> favouriteBooks = user.getFavouriteBooks();
        favouriteBooks.add(bookRepository.findById(bookId).get());
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Set<Book> findFavouriteBooks(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFavouriteBooks();
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public void removeFavouriteFor(User user, int bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        user.getFavouriteBooks().remove(book);
        userRepository.save(user);
    }
}
