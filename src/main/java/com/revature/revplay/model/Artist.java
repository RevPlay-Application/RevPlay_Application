package com.revature.revplay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_seq_gen")
    @SequenceGenerator(name = "artist_seq_gen", sequenceName = "artist_seq", allocationSize = 1)
    @Column(name = "artist_id")
    private Long artistId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "artist_name", nullable = false, length = 150)
    private String artistName;

    @Column(length = 100)
    private String genre;

    @Lob
    @Column(name = "banner_image")
    private byte[] bannerImage;

    @Column(length = 200)
    private String instagram;

    @Column(length = 200)
    private String twitter;

    @Column(length = 200)
    private String youtube;

    @Column(length = 200)
    private String website;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<Song> songs = new ArrayList<>();
}
