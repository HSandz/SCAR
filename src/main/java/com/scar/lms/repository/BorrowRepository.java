package com.scar.lms.repository;

import com.scar.lms.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {

    boolean existsByUserIdAndBookId(int userId, int bookId);

    Optional<Borrow> findByUserIdAndBookId(int userId, int bookId);

    List<Borrow> findAllByUserId(int userId);

    @Query("SELECT b FROM Borrow b WHERE FUNCTION('MONTH', b.borrowDate) = :month")
    List<Borrow> findAllByBorrowDateMonth(@Param("month") int month);

    @Query("SELECT COUNT(b) FROM Borrow b WHERE MONTH(b.borrowDate) = :month")
    Long countByBorrowDateMonth(@Param("month") int month);

    Long countByUserId(int userId);

    long count();
}
