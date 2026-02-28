package com.revature.revplay.repository;

import com.revature.revplay.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByGenre(Genre genre);

    List<Song> findByArtist(Artist artist);

    List<Song> findByAlbum(Album album);

    List<Song> findByVisibility(Visibility visibility);

    List<Song> findByTitleContainingIgnoreCaseAndVisibility(String title, Visibility visibility);

    List<Song> findByArtist_ArtistNameContainingIgnoreCaseAndVisibility(String artistName, Visibility visibility);

    List<Song> findByGenreAndVisibility(Genre genre, Visibility visibility);

    List<Song> findByAlbumAndVisibility(Album album, Visibility visibility);

    @Query("SELECT s FROM Song s WHERE YEAR(s.releaseDate) = :year")
    List<Song> findByReleaseDateYear(@Param("year") int year);
}
