package com.scar.lms.repository;

import com.scar.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    User findByUsernameAndEmail(String username, String email);

    User findByUsernameAndPassword(String username, String password);

}
