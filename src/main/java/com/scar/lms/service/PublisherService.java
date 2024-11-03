package com.scar.lms.service;

import com.scar.lms.entity.Publisher;

import java.util.List;

public interface PublisherService {

    List<Publisher> findAllPublishers();

    Publisher findPublisherById(int id);

    void createPublisher(Publisher publisher);

    void updatePublisher(Publisher publisher);

    void deletePublisher(int id);
}
