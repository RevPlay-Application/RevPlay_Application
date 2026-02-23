package com.revature.revplay.service;

import com.revature.revplay.model.Album;
import com.revature.revplay.model.Artist;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.Visibility;
import java.util.List;

public interface SongService {
    Song uploadSong(Song song);

    Song updateSong(Song song);

    Song getSongById(Long id);

    List<Song> getAllSongs();

    List<Song> getSongsByArtist(Artist artist);

    List<Song> getSongsByAlbum(Album album);

    List<Song> getSongsByGenre(String genre);

    List<Song> getSongsByVisibility(Visibility visibility);

    List<Song> searchSongs(String query);

    void deleteSong(Long id);

    void incrementPlayCount(Long id);

    Song getRandomSong();
}
