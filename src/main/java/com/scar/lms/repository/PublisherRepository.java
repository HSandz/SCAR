package com.scar.lms.repository;

import com.scar.lms.entity.Publisher;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {

    boolean existsByName(String name);
}
