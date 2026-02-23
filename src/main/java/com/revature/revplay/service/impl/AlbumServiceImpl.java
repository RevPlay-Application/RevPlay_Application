package com.revature.revplay.service.impl;

import com.revature.revplay.customexceptions.AlbumNotFoundException;
import com.revature.revplay.model.Album;
import com.revature.revplay.model.Artist;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    @Override
    @Transactional
    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    @Override
    @Transactional
    public Album updateAlbum(Album album) {
        if (!albumRepository.existsById(album.getAlbumId())) {
            throw new AlbumNotFoundException("Album not found with id: " + album.getAlbumId());
        }
        return albumRepository.save(album);
    }

    @Override
    public Album getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album not found with id: " + id));
    }

    @Override
    public List<Album> getAlbumsByArtist(Artist artist) {
        return albumRepository.findByArtist(artist);
    }

    @Override
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAlbum(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new AlbumNotFoundException("Album not found with id: " + id);
        }
        albumRepository.deleteById(id);
    }

    @Override
    public List<Album> searchAlbums(String query) {
        return albumRepository.findByAlbumNameContainingIgnoreCase(query);
    }
}
