package com.scar.lms.service;

import com.scar.lms.entity.User;
import com.scar.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplement implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImplement(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void saveUser(User user) {
        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public boolean userExists(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email) != null;
    }

    @Override
    public boolean validateAuthentication(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        User user = getUserByUsername(username);
        return user != null && bCryptPasswordEncoder.matches(password, user.getPassword());
    }

    @Override
    public boolean updateUserPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        saveUser(user);
        return true;
    }
}
