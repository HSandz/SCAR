package com.scar.lms.repository;

import com.scar.lms.entity.Notify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Integer> {
    List<Notify> findByUserIdAndIsReadFalse(int userId);
}
