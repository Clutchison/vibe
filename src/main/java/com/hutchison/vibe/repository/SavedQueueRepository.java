package com.hutchison.vibe.repository;

import com.hutchison.vibe.model.entity.SavedQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedQueueRepository extends JpaRepository<SavedQueue, Long> {

    List<SavedQueue> findByName(String name);

}
