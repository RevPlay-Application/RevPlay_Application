package com.revature.revplay.service;

import com.revature.revplay.model.Artist;
import com.revature.revplay.model.User;
import java.util.List;

public interface ArtistService {
    Artist registerArtist(Artist artist);

    Artist updateArtist(Artist artist);

    Artist getArtistById(Long id);

    Artist getArtistByUser(User user);

    List<Artist> getAllArtists();

    List<Artist> searchArtists(String name);
}
