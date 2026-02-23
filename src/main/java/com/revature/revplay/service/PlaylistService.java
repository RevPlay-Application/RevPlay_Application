package com.revature.revplay.service;

import com.revature.revplay.model.Playlist;
import com.revature.revplay.model.User;
import java.util.List;

public interface PlaylistService {
    Playlist createPlaylist(Playlist playlist);

    Playlist updatePlaylist(Playlist playlist);

    Playlist getPlaylistById(Long id);

    List<Playlist> getPlaylistsByUser(User user);

    List<Playlist> getPublicPlaylists();

    void deletePlaylist(Long id);

    void addSongToPlaylist(Long playlistId, Long songId);

    void removeSongFromPlaylist(Long playlistId, Long songId);

    List<Playlist> searchPlaylists(String query);
}
