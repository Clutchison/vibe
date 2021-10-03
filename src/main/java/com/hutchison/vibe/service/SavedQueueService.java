package com.hutchison.vibe.service;

import com.hutchison.vibe.exception.UnauthorizedException;
import com.hutchison.vibe.model.entity.OwnerPermission;
import com.hutchison.vibe.model.entity.SavedQueue;
import com.hutchison.vibe.model.entity.Track;
import com.hutchison.vibe.repository.SavedQueueRepository;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Transactional
@Service
public class SavedQueueService {

    private final SavedQueueRepository savedQueueRepository;

    @Autowired
    public SavedQueueService(SavedQueueRepository savedQueueRepository) {
        this.savedQueueRepository = savedQueueRepository;
    }

    public void createQueue(String queueName, User owner, List<AudioTrack> tracks) {
        List<Track> queueTracks = toTracks(tracks);
        savedQueueRepository.save(SavedQueue.builder()
                .name(queueName)
                .ownerPermissions(OwnerPermission.creatorPermission(owner.getIdLong(), owner.getName()))
                .tracks(queueTracks)
                .build());
    }

    public SavedQueue getSavedQueue(String queueName, Long ownerId) throws UnauthorizedException {
        List<SavedQueue> queues = savedQueueRepository.findByName(queueName);
        if(queues.size() == 1) {
            SavedQueue queue = queues.get(0);
            if(queue.canRead(ownerId)) {
                return queue;
            }
        }
        return null;
    }

    public void updateSavedQueue(String queueName, Long ownerId, List<AudioTrack> tracks) throws UnauthorizedException {
        SavedQueue savedQueue = getSavedQueue(queueName, ownerId);
        if(savedQueue.canUpdate(ownerId)) {
            savedQueue.getTracks().clear();
            savedQueue.getTracks().addAll(toTracks(tracks));
            savedQueueRepository.save(savedQueue);
        }
    }

    public void deleteSavedQueue(String queueName, Long ownerId) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName,ownerId);
        if(queue.canDelete(ownerId)) {
            savedQueueRepository.delete(queue);
        }
    }

    public boolean exists(String queueName) {
        List<SavedQueue> queues = savedQueueRepository.findByName(queueName);
        return (queues != null && !queues.isEmpty());
    }

    public void shareQueue(String queueName, Long sharerId, List<User> sharees) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName, sharerId);
        List<Long> permOwnerIds = queue.getOwnerPermissions().stream().map(OwnerPermission::getOwnerId).collect(Collectors.toList());
        if(queue.canShare(sharerId)) {
           sharees.forEach(sharee -> {
               if(!permOwnerIds.contains(sharee.getIdLong())) {
                   queue.getOwnerPermissions().add(OwnerPermission.defaultPermission(sharee.getIdLong(), sharee.getName()));
               }
           });
           savedQueueRepository.save(queue);
        }
    }

    public String getQueuePerms(String queueName, Long ownerId) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName, ownerId);
        StringBuilder sb = new StringBuilder("Queue \"" + queueName + "\" Permissions:\n");

        String permInfo = queue.getOwnerPermissions()
                .stream()
                .map(OwnerPermission::toString)
                .collect(Collectors.joining("\n"));
        sb.append(permInfo);
        return sb.toString();
    }

    public void updateQueuePerms(String queueName, Long ownerId, List<User> sharees, String perms) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName, ownerId);
        if(queue.canShare(ownerId)) {
            Map<Long, Integer> ownersIndexed = IntStream.range(0, queue.getOwnerPermissions().size()).boxed().collect(Collectors.toMap(i -> queue.getOwnerPermissions().get(i).getOwnerId(), i -> i));
            sharees.forEach(sharee -> {
                Integer idx = ownersIndexed.getOrDefault(sharee.getIdLong(), -1);
                OwnerPermission perm = null;
                if(idx != -1) {
                    perm = queue.getOwnerPermissions().get(idx);
                    perm.setRead(perms.contains("r") || perms.contains("u"));
                    perm.setUpdate(perms.contains("u"));
                    perm.setDelete(perms.contains("d"));
                }
                else {
                    queue.getOwnerPermissions().add(OwnerPermission.builder()
                            .username(sharee.getName())
                            .read(perms.contains("r") || perms.contains("u"))
                            .update(perms.contains("u"))
                            .delete(perms.contains("d"))
                            .build());
                }
            });
            savedQueueRepository.save(queue);
        }
    }

    private List<Track> toTracks(List<AudioTrack> tracks) {
        return tracks.stream().map(Track::toTrack).collect(Collectors.toList());
    }
}
