package com.scar.lms.service;

import com.scar.lms.entity.Rating;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RatingService {

    CompletableFuture<List<Rating>> getBookRatings(int bookId);

    CompletableFuture<List<Rating>> getBooksRatingsOfUser(int bookId, int userId);

    void saveRating(Rating rating);

    void deleteRating(Rating rating);
}
