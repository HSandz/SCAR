package com.scar.lms.service.impl;

import com.scar.lms.entity.Borrow;
import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.repository.BorrowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowServiceImplTest {

    @Mock
    private BorrowRepository borrowRepository;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsBookBorrowedBy() {
        when(borrowRepository.existsByUserIdAndBookId(1, 1)).thenReturn(true);
        boolean result = borrowService.isBookBorrowedBy(1, 1);
        assertTrue(result);
    }

    @Test
    void testAddBorrow() {
        Borrow borrow = new Borrow();
        borrow.setId(1);
        when(borrowRepository.existsById(1)).thenReturn(false);
        borrowService.addBorrow(borrow);
        verify(borrowRepository, times(1)).save(borrow);
    }

    @Test
    void testAddBorrowAlreadyExists() {
        Borrow borrow = new Borrow();
        borrow.setId(1);
        when(borrowRepository.existsById(1)).thenReturn(true);
        assertThrows(OperationNotAllowedException.class, () -> borrowService.addBorrow(borrow));
    }

    @Test
    void testUpdateBorrow() {
        Borrow borrow = new Borrow();
        borrowService.updateBorrow(borrow);
        verify(borrowRepository, times(1)).save(borrow);
    }

    @Test
    void testRemoveBorrow() {
        Borrow borrow = new Borrow();
        borrow.setId(1);
        when(borrowRepository.existsById(1)).thenReturn(true);
        borrowService.removeBorrow(borrow);
        verify(borrowRepository, times(1)).delete(borrow);
    }

    @Test
    void testRemoveBorrowNotExists() {
        Borrow borrow = new Borrow();
        borrow.setId(1);
        when(borrowRepository.existsById(1)).thenReturn(false);
        assertThrows(OperationNotAllowedException.class, () -> borrowService.removeBorrow(borrow));
    }

//    @Test
//    void testFindBorrow() {
//        Borrow borrow = new Borrow();
//        when(borrowRepository.findByUserIdAndBookId(1, 1)).thenReturn(Optional.of(borrow));
//        Optional<Borrow> result = borrowService.findBorrow(1, 1);
//        assertTrue(result.isPresent());
//        assertEquals(borrow, result.get());
//    }
//
//    @Test
//    void testFindBorrowsOfUser() {
//        List<Borrow> borrows = List.of(new Borrow(), new Borrow());
//        when(borrowRepository.findAllByUserId(1)).thenReturn(borrows);
//        List<Borrow> result = borrowService.findBorrowsOfUser(1);
//        assertEquals(borrows, result);
//    }
}