package com.scar.lms.service.impl;

import com.scar.lms.repository.BookRepository;
import com.scar.lms.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
