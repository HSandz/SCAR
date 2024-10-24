package com.scar.lms.service;

import com.scar.lms.entity.User;

public interface UserService {

    void saveUser(User user);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    boolean userExists(String username);

    boolean userExists(String email, String password);

    boolean validateAuthentication(String username, String password);

    public boolean updateUserPassword(String username, String newPassword);


}
