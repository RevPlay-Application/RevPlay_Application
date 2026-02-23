package com.revature.revplay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_seq_gen")
    @SequenceGenerator(name = "album_seq_gen", sequenceName = "album_seq", allocationSize = 1)
    @Column(name = "album_id")
    private Long albumId;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "album_name", nullable = false, length = 150)
    private String albumName;

    @Column(length = 500)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Lob
    @Column(name = "cover_image")
    private byte[] coverImage;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Song> songs = new ArrayList<>();
}
