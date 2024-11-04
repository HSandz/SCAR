package com.scar.lms.repository;

import com.scar.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.username LIKE %?1%")
    List<User> searchUsers(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
