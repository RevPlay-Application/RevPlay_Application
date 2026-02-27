package com.revature.revplay.controller;

import com.revature.revplay.model.*;
import com.revature.revplay.service.SongService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/artist/song")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping("/create")
    public String uploadSong(@ModelAttribute("authenticatedUser") User user,
                             @RequestParam String title,
                             @RequestParam Genre genre,
                             @RequestParam Integer duration,
                             @RequestParam(required = false) String releaseDate,
                             @RequestParam Visibility visibility,
                             @RequestParam(required = false) Long albumId,
                             @RequestParam MultipartFile audioFile) throws IOException {

        if (user == null) return "redirect:/login";

        songService.createSong(user, title, genre, duration,
                releaseDate, visibility, albumId, audioFile);

        return "redirect:/artist/dashboard?songCreated=true";
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<byte[]> playSong(@PathVariable Long id) {

        byte[] audio = songService.getSongAudio(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(audio);
    }

    @GetMapping("/{id}")
    public String viewSong(@PathVariable Long id,
                           @ModelAttribute("authenticatedUser") User user,
                           Model model) {

        if (user == null) return "redirect:/login";

        model.addAttribute("song", songService.getSongById(id));
        return "artist/song-details";
    }

    @PostMapping("/delete/{id}")
    public String deleteSong(@PathVariable Long id,
                             @ModelAttribute("authenticatedUser") User user) {

        if (user == null) return "redirect:/login";

        songService.deleteSong(id, user);

        return "redirect:/artist/dashboard?songDeleted=true";
    }
}