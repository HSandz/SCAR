package com.scar.lms.service.impl;

import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
