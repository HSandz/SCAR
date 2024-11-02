package com.scar.lms.service;

import com.scar.lms.entity.Publisher;

import java.util.List;

public interface PublisherService {

    public List<Publisher> findAllPublishers();

    public Publisher findPublisherById(int id);

    public void createPublisher(Publisher publisher);

    public void updatePublisher(Publisher publisher);

    public void deletePublisher(int id);
}
