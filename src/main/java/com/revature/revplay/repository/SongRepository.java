package com.revature.revplay.repository;

import com.revature.revplay.model.Album;
import com.revature.revplay.model.Artist;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByGenre(String genre);

    List<Song> findByArtist(Artist artist);

    List<Song> findByAlbum(Album album);

    List<Song> findByVisibility(Visibility visibility);

    List<Song> findByTitleContainingIgnoreCaseAndVisibility(String title, Visibility visibility);

    List<Song> findByArtist_ArtistNameContainingIgnoreCaseAndVisibility(String artistName, Visibility visibility);
}
