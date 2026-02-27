package com.revature.revplay.service.impl;

import com.revature.revplay.model.*;
import com.revature.revplay.repository.*;
import com.revature.revplay.service.SongService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public SongServiceImpl(SongRepository songRepository,
                           AlbumRepository albumRepository,
                           ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public void createSong(User user, String title, Genre genre, Integer duration,
                           String releaseDate, Visibility visibility,
                           Long albumId, MultipartFile audioFile) throws IOException {

        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Song song = new Song();
        song.setTitle(title);
        song.setGenre(genre);
        song.setDuration(duration);
        song.setVisibility(visibility);
        song.setArtist(artist);
        song.setPlayCount(0);

        if (albumId != null) {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("Album not found"));
            song.setAlbum(album);
        }

        if (releaseDate != null && !releaseDate.isEmpty()) {
            song.setReleaseDate(LocalDate.parse(releaseDate));
        }

        if (audioFile != null && !audioFile.isEmpty()) {
            song.setAudioFile(audioFile.getBytes());
        } else {
            throw new RuntimeException("Audio file is required");
        }

        songRepository.save(song);
    }

    @Override
    public byte[] getSongAudio(Long id) {
        Song song = getSongById(id);
        return song.getAudioFile();
    }

    @Override
    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));
    }

    @Override
    public List<Album> getAlbumsByArtist(User user) {
        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        return albumRepository.findByArtist(artist);
    }

    @Override
    public void updateSong(User user, Long songId, String title, Genre genre,
                           Integer duration, String releaseDate,
                           Visibility visibility, Long albumId,
                           MultipartFile audioFile) throws IOException {

        Song song = getSongById(songId);

        song.setTitle(title);
        song.setGenre(genre);
        song.setDuration(duration);
        song.setVisibility(visibility);

        if (releaseDate != null && !releaseDate.isEmpty()) {
            song.setReleaseDate(LocalDate.parse(releaseDate));
        }

        if (albumId != null) {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("Album not found"));
            song.setAlbum(album);
        } else {
            song.setAlbum(null);
        }

        if (audioFile != null && !audioFile.isEmpty()) {
            song.setAudioFile(audioFile.getBytes());
        }

        songRepository.save(song);
    }

    @Override
    @Transactional
    public void deleteSong(Long id, User user) {

        Song song = getSongById(id);

        if (!song.getArtist().getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        songRepository.delete(song);
    }
}