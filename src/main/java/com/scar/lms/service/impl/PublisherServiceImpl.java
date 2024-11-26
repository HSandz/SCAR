package com.scar.lms.service.impl;

import com.scar.lms.entity.Publisher;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.PublisherRepository;
import com.scar.lms.service.PublisherService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Publisher>> findAllPublishers() {
        return CompletableFuture.supplyAsync(publisherRepository::findAll);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Publisher> findPublisherById(int id) {
        return publisherRepository
                .findById(id)
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.failedFuture(
                        new ResourceNotFoundException("Publisher with ID not found: " + id)));
    }

    @Async
    @Override
    public void createPublisher(Publisher publisher) {
        if (publisherRepository.existsById(publisher.getId())) {
            throw new DuplicateResourceException("Publisher with ID " + publisher.getId() + " already exists");
        } else if (publisherRepository.existsByName(publisher.getName())) {
            throw new DuplicateResourceException("Publisher with name " + publisher.getName() + " already exists");
        }
        publisherRepository.save(publisher);
    }

    @Async
    @Override
    public void updatePublisher(Publisher publisher) {
        publisherRepository.save(publisher);
    }

    @Async
    @Override
    public void deletePublisher(int id) {
        var publisher = publisherRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Publisher with ID not found: " + id));
        publisherRepository.delete(publisher);
    }
}
