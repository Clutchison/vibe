package com.hutchison.vibe.repository;

import com.hutchison.vibe.model.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {

}
