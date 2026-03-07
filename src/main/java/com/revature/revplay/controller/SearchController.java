package com.revature.revplay.controller;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.Song;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This controller manages the entire search and music discovery ecosystem of
 * the application.
 * It provides users with powerful tools to find songs, artists, and albums
 * through keyword-based
 * searches and granular filtering. By handling specialized browsing categories
 * like genres,
 * it helps users discover content even when they don't have a specific track in
 * mind.
 * It coordinates with the SearchService to deliver high-performance querying
 * across
 * multiple database tables simultaneously.
 */


// ####################################### Person2 CODE START #########################################
@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public SearchController(SearchService searchService, AlbumRepository albumRepository, UserRepository userRepository) {
        this.searchService = searchService;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String search(@RequestParam String q, Model model) {
        SearchResultDto results = searchService.searchAll(q);
        model.addAttribute("query", q);
        model.addAttribute("results", results);
        return "search/results";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam(required = false) String title,
                        @RequestParam(required = false) String genre,
                        @RequestParam(required = false) Long artistId,
                        @RequestParam(required = false) Long albumId,
                        @RequestParam(required = false) Integer releaseYear,
                        Model model) {
        List<Song> songs = searchService.filterSongs(title, genre, artistId, albumId, releaseYear);
        model.addAttribute("songs", songs);
        model.addAttribute("genres", searchService.getAllGenres());
        model.addAttribute("artists", userRepository.findAllArtists());
        model.addAttribute("albums", albumRepository.findAll());
        model.addAttribute("selectedTitle", title);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedArtistId", artistId);
        model.addAttribute("selectedAlbumId", albumId);
        model.addAttribute("selectedReleaseYear", releaseYear);
        return "search/filter";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("genres", searchService.getAllGenres());
        model.addAttribute("artists", userRepository.findAllArtists());
        model.addAttribute("albums", albumRepository.findAllByOrderByReleaseDateDesc());
        return "search/categories";
    }
}

// ######################################## Person2 CODE END ##########################################
