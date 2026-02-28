package com.revature.revplay.controller;

import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.ArtistRepository;
import com.revature.revplay.repository.SongRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MusicDiscoveryController {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public MusicDiscoveryController(SongRepository songRepository, AlbumRepository albumRepository,
            ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    @GetMapping("/")
    public String browseMusicLibrary(Model model) {
        var songs = songRepository.findAll();
        var albums = albumRepository.findAll();
        var artists = artistRepository.findAll();

        model.addAttribute("songs", songs);
        model.addAttribute("albums", albums);
        model.addAttribute("artists", artists);

        // Extract unique genres
        var genres = songs.stream()
                .map(s -> s.getGenre())
                .filter(g -> g != null)
                .distinct()
                .toList();
        model.addAttribute("genres", genres);

        return "home";
    }
}
