package com.scar.lms.service.impl;

import com.scar.lms.entity.Publisher;
import com.scar.lms.exception.NotFoundException;
import com.scar.lms.repository.PublisherRepository;
import com.scar.lms.service.PublisherService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Publisher> findAllPublishers() {
        return publisherRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Publisher findPublisherById(int id) {
        return publisherRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Publisher with ID %d not found", id)
                        )
                );
    }

    @Override
    public void createPublisher(Publisher publisher) {
        publisherRepository.save(publisher);
    }

    @Override
    public void updatePublisher(Publisher publisher) {
        publisherRepository.save(publisher);
    }

    @Override
    public void deletePublisher(int id) {
        var publisher = publisherRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Publisher with ID %d not found", id)
                        )
                );
        publisherRepository.delete(publisher);
    }
}
