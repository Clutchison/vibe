package com.hutchison.vibe.model.entity;

import com.hutchison.vibe.exception.UnauthorizedException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

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

    public void canRead(Long ownerId) throws UnauthorizedException {
        can(p -> p.getOwnerId().equals(ownerId) && p.isRead());
    }

    public void canDelete(Long ownerId) throws UnauthorizedException {
        can(p -> p.getOwnerId().equals(ownerId) && p.isDelete());
    }

    public void canUpdate(Long ownerId) throws UnauthorizedException {
        can(p -> p.getOwnerId().equals(ownerId) && p.isUpdate());
    }

    public void canShare(Long ownerId) throws UnauthorizedException {
        can(p -> p.getOwnerId().equals(ownerId) && p.isCreator());
    }

    public void can(Predicate<OwnerPermission> p) throws UnauthorizedException {
        ownerPermissions
                .stream()
                .filter(p)
                .findFirst().orElseThrow(UnauthorizedException::new);
    }
}
