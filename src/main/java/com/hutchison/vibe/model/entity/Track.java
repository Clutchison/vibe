package com.hutchison.vibe.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

@Entity(name = "track")
@Table(name = "track")
@Builder
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, name = "track_id")
    Long trackId;

    @Column(name = "title")
    String title;

    @Column(name = "author")
    String author;

    @Column(name = "length")
    Long length;

    @Column(name = "load_id", nullable = false)
    String loadId;
}
