package com.revature.revplay.controller;

import com.revature.revplay.model.Song;
import com.revature.revplay.model.Visibility;
import com.revature.revplay.service.AlbumService;
import com.revature.revplay.service.ArtistService;
import com.revature.revplay.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SongService songService;
    private final AlbumService albumService;
    private final ArtistService artistService;

    @GetMapping("/")
    public String home(Model model) {
        List<Song> songs = songService.getSongsByVisibility(Visibility.PUBLIC);
        model.addAttribute("songs", songs);
        model.addAttribute("albums", albumService.getAllAlbums());
        model.addAttribute("artists", artistService.getAllArtists());
        return "home";
    }
}
