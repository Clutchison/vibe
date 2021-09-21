package com.hutchison.vibe.model.entity;

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

    @Column(nullable = false, name ="owner")
    String owner;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "saved_queue_id")
    List<Track> tracks;
}
