package com.scar.lms.service;

import com.scar.lms.entity.Borrow;

import java.util.List;
import java.util.Optional;

public interface BorrowService {

    boolean isBookBorrowedBy(int userId, int bookId);

    void addBorrow(Borrow borrow);

    void updateBorrow(Borrow borrow);

    void removeBorrow(Borrow borrow);

    Optional<Borrow> findBorrow(int bookId, int userId);

    List<Borrow> findAllBorrows(int userId);
}
