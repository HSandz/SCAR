package com.scar.lms.service;

import com.scar.lms.entity.Borrow;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface BorrowService {

    boolean isBookBorrowedBy(int userId, int bookId);

    void addBorrow(Borrow borrow);

    void updateBorrow(Borrow borrow);

    void removeBorrow(Borrow borrow);

    CompletableFuture<Optional<Borrow>> findBorrow(int bookId, int userId);

    CompletableFuture<List<Borrow>> findBorrowsOfUser(int userId);

    CompletableFuture<List<Borrow>> findAllBorrows();

    CompletableFuture<List<Borrow>> findBorrowsByMonth(int month);
    
    CompletableFuture<Long> countAllBorrows();

    CompletableFuture<Long> countBorrowsByUser(int userId);

    CompletableFuture<Long> countBorrowsByMonth(int i);
}
