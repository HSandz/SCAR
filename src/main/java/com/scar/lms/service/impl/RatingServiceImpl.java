package com.scar.lms.service.impl;

import com.scar.lms.entity.Rating;
import com.scar.lms.repository.RatingRepository;
import com.scar.lms.service.BookService;
import com.scar.lms.service.RatingService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final BookService bookService;

    public RatingServiceImpl(final RatingRepository ratingRepository,
                             final BookService bookService) {
        this.ratingRepository = ratingRepository;
        this.bookService = bookService;
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Rating>> getBookRatings(int bookId) {
        return CompletableFuture.supplyAsync(() -> ratingRepository.findByBookId(bookId));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Rating>> getBooksRatingsOfUser(int bookId, int userId) {
        return CompletableFuture.supplyAsync(() -> ratingRepository.findByBookIdAndUserId(bookId, userId));
    }

    @Async
    @Override
    public void saveRating(Rating rating) {
        ratingRepository.save(rating);
        bookService.updateBookRating(rating.getBook().getId(), ratingRepository.getAverageRating(rating.getBook().getId()));
    }

    @Async
    @Override
    public void deleteRating(Rating rating) {
        ratingRepository.delete(rating);
    }
}
