package com.revature.revplay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_seq_gen")
    @SequenceGenerator(name = "song_seq_gen", sequenceName = "song_seq", allocationSize = 1)
    @Column(name = "song_id")
    private Long songId;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 100)
    private String genre;

    private Integer duration;

    @Lob
    @Column(name = "audio_file")
    private byte[] audioFile;

    @Lob
    @Column(name = "cover_image")
    private byte[] coverImage;

    @Column(name = "play_count")
    private Integer playCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Visibility visibility;

    @Column(name = "release_date")
    private LocalDate releaseDate;
}
