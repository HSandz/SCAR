package com.scar.lms.service.impl;

import com.scar.lms.entity.Publisher;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllPublishers() {
        List<Publisher> publishers = List.of(new Publisher(), new Publisher());
        when(publisherRepository.findAll()).thenReturn(publishers);

        //List<Publisher> result = publisherService.findAllPublishers();
        //assertEquals(2, result.size());
        verify(publisherRepository, times(1)).findAll();
    }

    @Test
    void testFindPublisherById() {
        Publisher publisher = new Publisher();
        publisher.setId(1);
        when(publisherRepository.findById(1)).thenReturn(Optional.of(publisher));

        Publisher result = publisherService.findPublisherById(1);
        assertEquals(1, result.getId());
        verify(publisherRepository, times(1)).findById(1);
    }

    @Test
    void testFindPublisherByIdNotFound() {
        when(publisherRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publisherService.findPublisherById(1));
        verify(publisherRepository, times(1)).findById(1);
    }

    @Test
    void testCreatePublisher() {
        Publisher publisher = new Publisher();
        publisher.setId(1);
        publisher.setName("Test Publisher");

        when(publisherRepository.existsById(1)).thenReturn(false);
        when(publisherRepository.existsByName("Test Publisher")).thenReturn(false);

        publisherService.createPublisher(publisher);
        verify(publisherRepository, times(1)).save(publisher);
    }

    @Test
    void testCreatePublisherDuplicateId() {
        Publisher publisher = new Publisher();
        publisher.setId(1);

        when(publisherRepository.existsById(1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> publisherService.createPublisher(publisher));
        verify(publisherRepository, never()).save(publisher);
    }

    @Test
    void testCreatePublisherDuplicateName() {
        Publisher publisher = new Publisher();
        publisher.setName("Test Publisher");

        when(publisherRepository.existsByName("Test Publisher")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> publisherService.createPublisher(publisher));
        verify(publisherRepository, never()).save(publisher);
    }

    @Test
    void testUpdatePublisher() {
        Publisher publisher = new Publisher();
        publisher.setId(1);

        publisherService.updatePublisher(publisher);
        verify(publisherRepository, times(1)).save(publisher);
    }

    @Test
    void testDeletePublisher() {
        Publisher publisher = new Publisher();
        publisher.setId(1);
        when(publisherRepository.findById(1)).thenReturn(Optional.of(publisher));

        publisherService.deletePublisher(1);
        verify(publisherRepository, times(1)).delete(publisher);
    }

    @Test
    void testDeletePublisherNotFound() {
        when(publisherRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publisherService.deletePublisher(1));
        verify(publisherRepository, never()).delete(any());
    }
}