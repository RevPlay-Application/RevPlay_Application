package com.revature.revplay.controller;

import com.revature.revplay.customexceptions.ResourceNotFoundException;
import com.revature.revplay.model.*;
import com.revature.revplay.repository.*;
import com.revature.revplay.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/browse")
public class BrowseController {

    private final SearchService searchService;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    public BrowseController(SearchService searchService, SongRepository songRepository,
                            ArtistRepository artistRepository, AlbumRepository albumRepository) {
        this.searchService = searchService;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        model.addAttribute("query", q);
        model.addAttribute("songs", searchService.searchSongs(q));
        model.addAttribute("artists", searchService.searchArtists(q));
        model.addAttribute("albums", searchService.searchAlbums(q));
        model.addAttribute("playlists", searchService.searchPlaylists(q));
        return "browse/search";
    }

    @GetMapping("/genre/{genre}")
    public String browseByGenre(@PathVariable String genre, Model model) {
        model.addAttribute("genre", genre);
        model.addAttribute("songs", songRepository.findByGenreAndVisibility(genre, Visibility.PUBLIC));
        model.addAttribute("artists", artistRepository.findByGenreContainingIgnoreCase(genre));
        return "browse/genre";
    }

    @GetMapping("/artist/{id}")
    public String viewArtist(@PathVariable Long id, Model model) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        model.addAttribute("artist", artist);
        model.addAttribute("songs", songRepository.findByArtist(artist));
        model.addAttribute("albums", albumRepository.findByArtist(artist));
        return "browse/artist";
    }

    @GetMapping("/album/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        model.addAttribute("album", album);
        model.addAttribute("songs", songRepository.findByAlbumAndVisibility(album, Visibility.PUBLIC));
        return "browse/album";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam(required = false) String genre,
                         @RequestParam(required = false) Long artistId,
                         @RequestParam(required = false) Long albumId,
                         @RequestParam(required = false) Integer year,
                         Model model) {
        List<Song> songs;
        if (genre != null) {
            songs = songRepository.findByGenreAndVisibility(genre, Visibility.PUBLIC);
        } else if (artistId != null) {
            Artist artist = artistRepository.findById(artistId).orElseThrow();
            songs = songRepository.findByArtist(artist);
        } else if (albumId != null) {
            Album album = albumRepository.findById(albumId).orElseThrow();
            songs = songRepository.findByAlbumAndVisibility(album, Visibility.PUBLIC);
        } else if (year != null) {
            songs = songRepository.findByReleaseDateYear(year);
        } else {
            songs = songRepository.findByVisibility(Visibility.PUBLIC);
        }
        model.addAttribute("songs", songs);
        model.addAttribute("genres", songRepository.findAll().stream()
                .map(Song::getGenre).filter(g -> g != null).distinct().toList());
        model.addAttribute("artists", artistRepository.findAll());
        model.addAttribute("albums", albumRepository.findAll());
        return "browse/filter";
    }
}
