package com.scar.lms.repository;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {

    boolean existsByUserIdAndBookId(int userId, int bookId);

    Optional<Borrow> findByUserIdAndBookId(int userId, int bookId);

    List<Borrow> findAllByUserId(int userId);
}
