package com.revature.revplay.service;

import com.revature.revplay.model.*;
import java.util.List;

public interface SearchService {
    List<Song> searchSongs(String keyword);
    List<Artist> searchArtists(String keyword);
    List<Album> searchAlbums(String keyword);
    List<Playlist> searchPlaylists(String keyword);
}
