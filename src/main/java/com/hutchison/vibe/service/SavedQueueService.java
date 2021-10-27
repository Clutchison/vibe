package com.hutchison.vibe.service;

import com.hutchison.vibe.config.ServerInfo;
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

import javax.servlet.ServletContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Transactional
@Service
public class SavedQueueService {

    private final SavedQueueRepository savedQueueRepository;
    private final ServletContext servletContext;

    @Autowired
    public SavedQueueService(SavedQueueRepository savedQueueRepository, ServletContext servletContext) {
        this.savedQueueRepository = savedQueueRepository;
        this.servletContext = servletContext;
    }

    public void createQueue(String queueName, User owner, List<AudioTrack> tracks) {
        List<Track> queueTracks = toTracks(tracks);
        savedQueueRepository.save(SavedQueue.builder()
                .name(queueName)
                .ownerPermissions(OwnerPermission.creatorPermission(owner.getIdLong(), owner.getName()))
                .tracks(queueTracks)
                .isPublic(false)
                .build());
    }

    public SavedQueue createPublicQueue(User owner, List<AudioTrack> tracks) {
        List<Track> queueTracks = toTracks(tracks);
        String uuid = UUID.randomUUID().toString();
        return savedQueueRepository.save(SavedQueue.builder()
                .ownerPermissions(OwnerPermission.creatorPermission(owner.getIdLong(), owner.getName()))
                .name(uuid)
                .tracks(queueTracks)
                .isPublic(true)
                .url(getURL(uuid))
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build());
    }

    public SavedQueue getSavedQueue(String queueName, Long ownerId) throws UnauthorizedException {
        List<SavedQueue> queues = savedQueueRepository.findByName(queueName);
        if(queues.size() == 1) {
            SavedQueue queue = queues.get(0);
            queue.canRead(ownerId);
            return queue;
        }
        return null;
    }

    public void updateSavedQueue(String queueName, Long ownerId, List<AudioTrack> tracks) throws UnauthorizedException {
        SavedQueue savedQueue = getSavedQueue(queueName, ownerId);
        savedQueue.canUpdate(ownerId);
        savedQueue.getTracks().clear();
        savedQueue.getTracks().addAll(toTracks(tracks));
        savedQueueRepository.save(savedQueue);
    }

    public void deleteSavedQueue(String queueName, Long ownerId) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName,ownerId);
        queue.canDelete(ownerId);
        savedQueueRepository.delete(queue);
    }

    public boolean exists(String queueName) {
        List<SavedQueue> queues = savedQueueRepository.findByName(queueName);
        return (queues != null && !queues.isEmpty());
    }

    public void shareQueue(String queueName, Long sharerId, List<User> sharees) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName, sharerId);
        List<Long> permOwnerIds = queue.getOwnerPermissions().stream().map(OwnerPermission::getOwnerId).collect(Collectors.toList());
        queue.canShare(sharerId);
        sharees.forEach(sharee -> {
            if(!permOwnerIds.contains(sharee.getIdLong())) {
                queue.getOwnerPermissions().add(OwnerPermission.defaultPermission(sharee.getIdLong(), sharee.getName()));
            }
        });
        savedQueueRepository.save(queue);
    }

    public String getQueuePerms(String queueName, Long ownerId) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName, ownerId);
        StringBuilder sb = new StringBuilder("Queue \"" + queueName + "\" Permissions:\n\n");

        String permInfo = queue.getOwnerPermissions()
                .stream()
                .map(OwnerPermission::emojiPrettyPrint)
                .collect(Collectors.joining("\n"));
        sb.append(permInfo);
        return sb.toString();
    }

    public void updateQueuePerms(String queueName, Long ownerId, List<User> sharees, String perms) throws UnauthorizedException {
        SavedQueue queue = getSavedQueue(queueName, ownerId);
        queue.canShare(ownerId);
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

    private List<Track> toTracks(List<AudioTrack> tracks) {
        return tracks.stream().map(Track::toTrack).collect(Collectors.toList());
    }

    private String getURL(String uuid) {
        String contextPath = servletContext.getContextPath();
        return "http://" + ServerInfo.getHostname() + ":" + ServerInfo.getPort() + contextPath + "/" + uuid + "/test";
    }
}
