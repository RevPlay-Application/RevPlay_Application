package com.revature.revplay.service.impl;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.Song;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    public SearchServiceImpl(SongRepository songRepository, UserRepository userRepository,
            AlbumRepository albumRepository, PlaylistRepository playlistRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
    }

    @Override
    public SearchResultDto searchAll(String keyword) {
        SearchResultDto results = new SearchResultDto();

        if (keyword != null && !keyword.trim().isEmpty()) {
            results.setSongs(songRepository.findByTitleContainingIgnoreCase(keyword.trim()));
            results.setArtists(
                    userRepository.findByDisplayNameContainingIgnoreCaseAndRole(keyword.trim(), Role.ARTIST));
            results.setAlbums(albumRepository.findByNameContainingIgnoreCase(keyword.trim()));
            results.setPlaylists(playlistRepository.findByNameContainingIgnoreCaseAndIsPublicTrue(keyword.trim()));
        }

        return results;
    }

    @Override
    public List<Song> filterSongs(String title, String genre, Long artistId, Long albumId, Integer releaseYear) {
        return songRepository.filterSongs(title, genre, artistId, albumId, releaseYear);
    }

    @Override
    public List<String> getAllGenres() {
        return songRepository.findAll().stream()
                .map(Song::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
