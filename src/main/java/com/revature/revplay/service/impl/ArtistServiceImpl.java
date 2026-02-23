package com.revature.revplay.service.impl;

import com.revature.revplay.customexceptions.ArtistNotFoundException;
import com.revature.revplay.model.Artist;
import com.revature.revplay.model.User;
import com.revature.revplay.repository.ArtistRepository;
import com.revature.revplay.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    @Override
    @Transactional
    public Artist registerArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public Artist updateArtist(Artist artist) {
        if (!artistRepository.existsById(artist.getArtistId())) {
            throw new ArtistNotFoundException("Artist not found with id: " + artist.getArtistId());
        }
        return artistRepository.save(artist);
    }

    @Override
    public Artist getArtistById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException("Artist not found with id: " + id));
    }

    @Override
    public Artist getArtistByUser(User user) {
        return artistRepository.findByUser(user)
                .orElseThrow(() -> new ArtistNotFoundException("Artist not found for user: " + user.getUsername()));
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    @Override
    public List<Artist> searchArtists(String name) {
        // Basic search by name containing
        return artistRepository.findAll().stream()
                .filter(a -> a.getArtistName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}
