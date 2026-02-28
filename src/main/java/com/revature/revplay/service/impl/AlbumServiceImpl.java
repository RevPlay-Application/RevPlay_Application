package com.revature.revplay.service.impl;

import com.revature.revplay.model.*;
import com.revature.revplay.repository.*;
import com.revature.revplay.service.AlbumService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository,
                            ArtistRepository artistRepository,
                            SongRepository songRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
    }

    @Override
    public void createAlbum(User user, String albumName, String description,
                            Genre genre, String releaseDate,
                            MultipartFile coverImage) throws IOException {

        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Album album = new Album();
        album.setAlbumName(albumName);
        album.setDescription(description);
        album.setGenre(genre);
        album.setArtist(artist);

        if (releaseDate != null && !releaseDate.isEmpty()) {
            album.setReleaseDate(LocalDate.parse(releaseDate));
        }

        if (coverImage != null && !coverImage.isEmpty()) {
            album.setCoverImage(coverImage.getBytes());
        }

        albumRepository.save(album);
    }

    @Override
    public Album getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
    }

    @Override
    public void updateAlbum(Long albumId, String albumName,
                            Genre genre, String description,
                            MultipartFile coverImage) throws IOException {

        Album album = getAlbumById(albumId);

        album.setAlbumName(albumName);
        album.setGenre(genre);
        album.setDescription(description);

        if (coverImage != null && !coverImage.isEmpty()) {
            album.setCoverImage(coverImage.getBytes());
        }

        albumRepository.save(album);
    }

    @Override
    public List<Song> getSongsByAlbum(Long albumId) {
        Album album = getAlbumById(albumId);
        return songRepository.findByAlbum(album);
    }

    @Override
    public byte[] getAlbumCover(Long id) {
        Album album = getAlbumById(id);

        if (album.getCoverImage() == null) {
            throw new RuntimeException("Cover not found");
        }

        return album.getCoverImage();
    }

    @Override
    @Transactional
    public void deleteAlbum(Long id, User user) {

        Album album = getAlbumById(id);

        if (!album.getArtist().getUser().getUserId()
                .equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        albumRepository.delete(album);
    }
}