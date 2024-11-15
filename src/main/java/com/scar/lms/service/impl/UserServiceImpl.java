package com.scar.lms.service.impl;

import com.scar.lms.entity.User;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("User with username not found: " + username));
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

    @Transactional
    @Override
    public void createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("User already existed");
        }
        System.out.println("Saving user: " + user);
        userRepository.saveAndFlush(user);
        System.out.println("User saved.");
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {
        var user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id not found: " + id));
        userRepository.delete(user);
    }
}
