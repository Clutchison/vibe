package com.hutchison.vibe.model.entity;

import com.hutchison.vibe.exception.UnauthorizedException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

@Entity(name = "saved_queue")
@Table(name = "saved_queue")
@Builder
public class SavedQueue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, name = "saved_queue_id")
    Long queueId;

    @Column(nullable = false, name = "name")
    String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "saved_queue_id")
    List<OwnerPermission> ownerPermissions;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "saved_queue_id")
    List<Track> tracks;

    public boolean canRead(Long ownerId) throws UnauthorizedException {
        return ownerPermissions
                .stream()
                .filter(p -> p.getOwnerId().equals(ownerId) && p.isRead())
                .map(OwnerPermission::isRead)
                .findFirst().orElseThrow(UnauthorizedException::new);
    }

    public boolean canDelete(Long ownerId) throws UnauthorizedException {
        return ownerPermissions
                .stream()
                .filter(p -> p.getOwnerId().equals(ownerId) && p.isDelete())
                .map(OwnerPermission::isDelete)
                .findFirst().orElseThrow(UnauthorizedException::new);
    }

    public boolean canUpdate(Long ownerId) throws UnauthorizedException {
        return ownerPermissions
                .stream()
                .filter(p -> p.getOwnerId().equals(ownerId) && p.isUpdate())
                .map(OwnerPermission::isUpdate)
                .findFirst().orElseThrow(UnauthorizedException::new);
    }

    public boolean canShare(Long ownerId) throws UnauthorizedException {
        return ownerPermissions
                .stream()
                .filter(p -> p.getOwnerId().equals(ownerId) && p.isCreator())
                .map(OwnerPermission::isCreator)
                .findFirst().orElseThrow(UnauthorizedException::new);
    }
}
