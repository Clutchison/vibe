package com.hutchison.vibe.service;

import com.hutchison.vibe.model.entity.SavedQueue;
import com.hutchison.vibe.model.entity.Track;
import com.hutchison.vibe.repository.SavedQueueRepository;
import com.hutchison.vibe.repository.TrackRepository;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedQueueService {

    private final SavedQueueRepository savedQueueRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public SavedQueueService(SavedQueueRepository savedQueueRepository, TrackRepository trackRepository) {
        this.savedQueueRepository = savedQueueRepository;
        this.trackRepository = trackRepository;
    }

    public void createQueue(String queueName, String owner, List<AudioTrack> tracks) {
        List<Track> queueTracks = toTracks(tracks);
        savedQueueRepository.save(SavedQueue.builder()
                .name(queueName)
                .owner(owner)
                .tracks(queueTracks)
                .build());
    }

    public SavedQueue getSavedQueue(String queueName, String owner) {
        return savedQueueRepository.findOneByNameAndOwner(queueName, owner);
    }

    public boolean updateSavedQueue(String queueName, String owner, List<AudioTrack> tracks) {
        SavedQueue savedQueue = getSavedQueue(queueName, owner);
        savedQueue.setTracks(toTracks(tracks));
        savedQueue = savedQueueRepository.save(savedQueue);
        return true;
    }

    public void deleteSavedQueue(String queueName, String owner) {
        SavedQueue queue = savedQueueRepository.findOneByNameAndOwner(queueName, owner);
        savedQueueRepository.delete(queue);
    }

    public boolean exists(String queueName, String owner) {
        return savedQueueRepository.existsQueueByNameAndOwner(queueName, owner);
    }

    private List<Track> toTracks(List<AudioTrack> tracks) {
        return tracks.stream().map(t -> Track.builder()
                .title(t.getInfo().title)
                .author(t.getInfo().author)
                .length(t.getInfo().length)
                .loadId(t.getIdentifier()).build()
        ).collect(Collectors.toList());
    }
}
