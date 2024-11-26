package com.scar.lms.repository;

import com.scar.lms.entity.Role;
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

    @Query("SELECT u FROM User u WHERE u.email LIKE CONCAT('%', ?1, '@gmail.com')")
    Optional<User> findByGmail(String gmail);

    List<User> findByRole(Role role);

    Long countByRole(Role role);

    @Query("SELECT COUNT(b) FROM User u JOIN u.favouriteBooks b WHERE u.id = ?1")
    Long countFavouritesByUserId(int userId);
}
