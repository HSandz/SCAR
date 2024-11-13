package com.scar.lms.service.impl;

import com.scar.lms.entity.Borrow;
import com.scar.lms.repository.BorrowRepository;
import com.scar.lms.service.BorrowService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;

    public BorrowServiceImpl(final BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    @Override
    public boolean isBookBorrowedBy(int userId, int bookId) {
        return borrowRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Override
    public void addBorrow(Borrow borrow) {
        borrowRepository.save(borrow);
    }

    @Override
    public void updateBorrow(Borrow borrow) {
        borrowRepository.save(borrow);
    }

    @Override
    public void removeBorrow(Borrow borrow) {
        borrowRepository.delete(borrow);
    }

    @Override
    public Optional<Borrow> findBorrow(int bookId, int userId) {
        return borrowRepository.findByUserIdAndBookId(userId, bookId);
    }

    @Override
    public List<Borrow> findAllBorrows(int userId) {
        return borrowRepository.findAllByUserId(userId);
    }
}