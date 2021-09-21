package com.hutchison.vibe.repository;

import com.hutchison.vibe.model.entity.SavedQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedQueueRepository extends JpaRepository<SavedQueue, Long> {

    SavedQueue findOneByNameAndOwner(String name, String owner);

    boolean existsQueueByNameAndOwner(String name, String owner);

}
