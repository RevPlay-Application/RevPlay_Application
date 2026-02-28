package com.revature.revplay.controller;

import com.revature.revplay.model.*;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.ArtistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.service.ProfileManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ArtistAccountController {

    private final ProfileManagementService profileManagementService;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    public ArtistAccountController(
            ProfileManagementService profileManagementService,
            ArtistRepository artistRepository,
            AlbumRepository albumRepository,
            SongRepository songRepository) {

        this.profileManagementService = profileManagementService;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    @GetMapping("/artist/dashboard")
    public String showArtistDashboard(
            @ModelAttribute("authenticatedUser") User user,
            Model model) {

        if (user == null)
            return "redirect:/login";

        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        model.addAttribute("artist", artist);
        model.addAttribute("albums", artist.getAlbums());
        model.addAttribute("songs", artist.getSongs());

        return "artist/dashboard";
    }

    @GetMapping("/artist/song/upload")
    public String showUploadSongPage(
            @ModelAttribute("authenticatedUser") User user,
            Model model) {

        if (user == null)
            return "redirect:/login";

        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        model.addAttribute("albums", artist.getAlbums());

        return "artist/upload-song";
    }

    @GetMapping("/artist/album/create")
    public String showCreateAlbumPage(
            @ModelAttribute("authenticatedUser") User user) {

        if (user == null)
            return "redirect:/login";

        return "artist/create-album";
    }

    @PostMapping("/artist/profile/update")
    public String updateArtistProfile(
            @ModelAttribute("authenticatedUser") User user,
            @RequestParam String artistName,
            @RequestParam String bio,
            @RequestParam String genre,
            @RequestParam(required = false) String instagram,
            @RequestParam(required = false) String twitter,
            @RequestParam(required = false) String youtube,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) MultipartFile imageFile) {

        if (user == null)
            return "redirect:/login";

        profileManagementService.updateArtistProfile(
                user.getUserId(),
                artistName,
                bio,
                genre,
                instagram,
                twitter,
                youtube,
                website,
                imageFile
        );

        return "redirect:/artist/dashboard?updated=true";
    }




}