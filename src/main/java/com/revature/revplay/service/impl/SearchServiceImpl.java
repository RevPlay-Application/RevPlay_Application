package com.revature.revplay.service.impl;

import com.revature.revplay.model.*;
import com.revature.revplay.repository.*;
import com.revature.revplay.service.SearchService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    public SearchServiceImpl(SongRepository songRepository, ArtistRepository artistRepository,
                             AlbumRepository albumRepository, PlaylistRepository playlistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
    }

    @Override
    public List<Song> searchSongs(String keyword) {
        return songRepository.findByTitleContainingIgnoreCaseAndVisibility(keyword, Visibility.PUBLIC);
    }

    @Override
    public List<Artist> searchArtists(String keyword) {
        var byName = artistRepository.findByArtistNameContainingIgnoreCase(keyword);
        var byGenre = artistRepository.findByGenreContainingIgnoreCase(keyword);
        byName.addAll(byGenre);
        return byName.stream().distinct().toList();
    }

    @Override
    public List<Album> searchAlbums(String keyword) {
        return albumRepository.findByAlbumNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Playlist> searchPlaylists(String keyword) {
        return playlistRepository.findByNameContainingIgnoreCaseAndPrivacy(keyword, Privacy.PUBLIC);
    }
}
