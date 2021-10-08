package com.hutchison.vibe.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

@Entity(name = "owner_permission")
@Table(name = "owner_permission")
@Builder
public class OwnerPermission {

    static final String CHECK = ":white_check_mark:";
    static final String NO = ":no_entry:";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, name = "owner_permission_id")
    Long ownerPermissionId;

    @Column
    Long ownerId;

    @Column
    String username;

    @Column
    boolean creator;

    @Column
    boolean read;

    @Column
    boolean update;

    @Column
    boolean delete;

    @ManyToOne
    @JoinColumn(name = "saved_queue_id")
    SavedQueue savedQueue;

    public static List<OwnerPermission> creatorPermission(Long ownerId, String username) {
        return Collections.singletonList(OwnerPermission.builder()
                .creator(true)
                .read(true)
                .update(true)
                .delete(true)
                .username(username)
                .ownerId(ownerId).build());
    }

    public static OwnerPermission defaultPermission(Long ownerId, String username) {
        return OwnerPermission.builder()
                .read(true)
                .username(username)
                .ownerId(ownerId)
                .build();
    }

    @Override
    public String toString() {
        return "Username: " + username + ", Read: " + read + ", Update: " + update + ", " + "Delete: " + delete + ", Creator: " + creator;
    }

    public String emojiPrettyPrint() {
        return "Username: " + username + "\n\t" +
                "Read: " + (read ? CHECK : NO) + " " +
                "Update: " + (update ? CHECK : NO) + " " +
                "Delete: " + (delete ? CHECK : NO) + " " +
                "Creator: " + (creator ? CHECK : NO);
    }
}
