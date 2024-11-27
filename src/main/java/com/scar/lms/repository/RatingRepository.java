package com.scar.lms.repository;

import com.scar.lms.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    List<Rating> findByBookId(int bookId);

    List<Rating> findByBookIdAndUserId(int bookId, int userId);
}
