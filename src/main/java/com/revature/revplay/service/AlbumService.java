package com.revature.revplay.service;

import com.revature.revplay.model.Album;
import com.revature.revplay.model.Artist;
import java.util.List;

public interface AlbumService {
    Album createAlbum(Album album);

    Album updateAlbum(Album album);

    Album getAlbumById(Long id);

    List<Album> getAlbumsByArtist(Artist artist);

    List<Album> getAllAlbums();

    void deleteAlbum(Long id);

    List<Album> searchAlbums(String query);
}
