package com.revature.revplay.service.impl;

import com.revature.revplay.customexceptions.PlaylistNotFoundException;
import com.revature.revplay.customexceptions.SongNotFoundException;
import com.revature.revplay.model.Playlist;
import com.revature.revplay.model.PlaylistSong;
import com.revature.revplay.model.Privacy;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.User;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    @Override
    @Transactional
    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @Override
    @Transactional
    public Playlist updatePlaylist(Playlist playlist) {
        if (!playlistRepository.existsById(playlist.getPlaylistId())) {
            throw new PlaylistNotFoundException("Playlist not found with id: " + playlist.getPlaylistId());
        }
        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found with id: " + id));
    }

    @Override
    public List<Playlist> getPlaylistsByUser(User user) {
        return playlistRepository.findByUser(user);
    }

    @Override
    public List<Playlist> getPublicPlaylists() {
        return playlistRepository.findByPrivacy(Privacy.PUBLIC);
    }

    @Override
    @Transactional
    public void deletePlaylist(Long id) {
        if (!playlistRepository.existsById(id)) {
            throw new PlaylistNotFoundException("Playlist not found with id: " + id);
        }
        playlistRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = getPlaylistById(playlistId);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new SongNotFoundException("Song not found with id: " + songId));

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSong(song);
        ps.setPosition(playlist.getPlaylistSongs().size() + 1);

        playlist.getPlaylistSongs().add(ps);
        playlistRepository.save(playlist);
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = getPlaylistById(playlistId);
        playlist.getPlaylistSongs().removeIf(ps -> ps.getSong().getSongId().equals(songId));
        playlistRepository.save(playlist);
    }

    @Override
    public List<Playlist> searchPlaylists(String query) {
        return playlistRepository.findByNameContainingIgnoreCaseAndPrivacy(query, Privacy.PUBLIC);
    }
}
