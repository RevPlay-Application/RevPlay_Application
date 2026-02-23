package com.revature.revplay.repository;

import com.revature.revplay.model.Album;
import com.revature.revplay.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtist(Artist artist);

    List<Album> findByAlbumNameContainingIgnoreCase(String albumName);
}
