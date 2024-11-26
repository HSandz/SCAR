package com.scar.lms.service;

import com.scar.lms.entity.Publisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PublisherService {

    CompletableFuture<List<Publisher>> findAllPublishers();

    CompletableFuture<Publisher> findPublisherById(int id);

    void createPublisher(Publisher publisher);

    void updatePublisher(Publisher publisher);

    void deletePublisher(int id);
}
