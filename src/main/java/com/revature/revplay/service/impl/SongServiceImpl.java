package com.revature.revplay.service.impl;

import com.revature.revplay.customexceptions.SongNotFoundException;
import com.revature.revplay.model.Album;
import com.revature.revplay.model.Artist;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.Visibility;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Override
    @Transactional
    public Song uploadSong(Song song) {
        return songRepository.save(song);
    }

    @Override
    @Transactional
    public Song updateSong(Song song) {
        if (!songRepository.existsById(song.getSongId())) {
            throw new SongNotFoundException("Song not found with id: " + song.getSongId());
        }
        return songRepository.save(song);
    }

    @Override
    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new SongNotFoundException("Song not found with id: " + id));
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Override
    public List<Song> getSongsByArtist(Artist artist) {
        return songRepository.findByArtist(artist);
    }

    @Override
    public List<Song> getSongsByAlbum(Album album) {
        return songRepository.findByAlbum(album);
    }

    @Override
    public List<Song> getSongsByGenre(String genre) {
        return songRepository.findByGenre(genre);
    }

    @Override
    public List<Song> getSongsByVisibility(Visibility visibility) {
        return songRepository.findByVisibility(visibility);
    }

    @Override
    public List<Song> searchSongs(String query) {
        List<Song> byTitle = songRepository.findByTitleContainingIgnoreCaseAndVisibility(query, Visibility.PUBLIC);
        List<Song> byArtist = songRepository.findByArtist_ArtistNameContainingIgnoreCaseAndVisibility(query,
                Visibility.PUBLIC);

        // Combine and dedup
        byTitle.addAll(byArtist);
        return byTitle.stream().distinct().toList();
    }

    @Override
    @Transactional
    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new SongNotFoundException("Song not found with id: " + id);
        }
        songRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void incrementPlayCount(Long id) {
        Song song = getSongById(id);
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);
        // Note: In production with Oracle, we would call the stored procedure:
        // jdbcCall.withProcedureName("increment_play_count").execute(id);
    }

    @Override
    public Song getRandomSong() {
        List<Song> songs = songRepository.findByVisibility(Visibility.PUBLIC);
        if (songs.isEmpty())
            return null;
        return songs.get(new java.util.Random().nextInt(songs.size()));
    }
}
