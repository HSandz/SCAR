package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.User;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        //assertTrue(userService.findAllUsers().isEmpty());
        //verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertEquals(user, userService.findUserByUsername("testuser"));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindUserByUsernameNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserByUsername("testuser"));
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setId(1);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.existsById(1)).thenReturn(false);
        userService.createUser(user);
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void testCreateUserDuplicateUsername() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(user));
    }

    @Test
    void testAddFavouriteFor() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        Book book = new Book();
        book.setId(1);

        Set<Book> favouriteBooks = new HashSet<>();
        user.setFavouriteBooks(favouriteBooks);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        userService.addFavouriteFor(user, 1);

        assertTrue(user.getFavouriteBooks().contains(book), "Book should be added to user's favourites");
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void testAddFavouriteForUserNotFound() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        when(userRepository.existsById(1)).thenReturn(false);
        assertThrows(OperationNotAllowedException.class, () -> userService.addFavouriteFor(user, 1));
    }

    @Test
    void testFindFavouriteBooks() {
        User user = new User();
        user.setId(1);
        Set<Book> favouriteBooks = Collections.singleton(new Book());
        user.setFavouriteBooks(favouriteBooks);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        assertEquals(favouriteBooks, userService.findFavouriteBooks(1));
    }

    @Test
    void testRemoveFavouriteFor() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        Book book = new Book();
        book.setId(1);

        Set<Book> favouriteBooks = new HashSet<>();
        favouriteBooks.add(book);
        user.setFavouriteBooks(favouriteBooks);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        userService.removeFavouriteFor(user, 1);

        assertFalse(user.getFavouriteBooks().contains(book), "Book should be removed from user's favourites");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRemoveFavouriteForBookNotInFavorites() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        Book book = new Book();
        book.setId(1);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        assertThrows(OperationNotAllowedException.class, () -> userService.removeFavouriteFor(user, 1));
    }

    @Test
    void testRemoveFavouriteForUserNotFound() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(OperationNotAllowedException.class, () -> userService.removeFavouriteFor(user, 1));
    }
}