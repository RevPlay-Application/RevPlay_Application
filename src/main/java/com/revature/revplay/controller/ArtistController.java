package com.revature.revplay.controller;

import com.revature.revplay.model.*;
import com.revature.revplay.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import com.revature.revplay.customexceptions.ArtistNotFoundException;

@Controller
@RequestMapping("/artist")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;
    private final AlbumService albumService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);

        model.addAttribute("artist", artist);
        // Handle case where songs/albums might be null if new artist
        model.addAttribute("songs", songService.getSongsByArtist(artist));
        model.addAttribute("albums", albumService.getAlbumsByArtist(artist));

        return "artist/dashboard";
    }

    @GetMapping("/upload")
    public String showUploadForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);

        model.addAttribute("song", new Song());
        model.addAttribute("albums", albumService.getAlbumsByArtist(artist));
        return "artist/upload-song";
    }

    @PostMapping("/upload")
    public String uploadSong(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Song song,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "albumId", required = false) Long albumId) throws IOException {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);

        song.setArtist(artist);
        song.setAudioFile(file.getBytes());
        if (cover != null && !cover.isEmpty()) {
            song.setCoverImage(cover.getBytes());
        }
        song.setReleaseDate(LocalDate.now());
        song.setVisibility(Visibility.PUBLIC); // Default

        if (albumId != null) {
            Album album = albumService.getAlbumById(albumId);
            song.setAlbum(album);
        }

        songService.uploadSong(song);
        return "redirect:/artist/dashboard";
    }

    @GetMapping("/create-album")
    public String showAlbumForm(Model model) {
        model.addAttribute("album", new Album());
        return "artist/create-album";
    }

    @PostMapping("/create-album")
    public String createAlbum(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Album album,
            @RequestParam("cover") MultipartFile cover) throws IOException {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);

        album.setArtist(artist);
        album.setCoverImage(cover.getBytes());
        album.setReleaseDate(LocalDate.now());

        albumService.createAlbum(album);
        return "redirect:/artist/dashboard";
    }

    @GetMapping("/settings")
    public String showSettings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        model.addAttribute("artist", artist);
        return "artist/settings";
    }

    @GetMapping("/edit-profile")
    public String showEditProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        model.addAttribute("artist", artist);
        return "artist/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String updateArtistProfile(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Artist artist,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            @RequestParam(value = "removeProfileImage", defaultValue = "false") boolean removeProfileImage)
            throws IOException {

        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist currentArtist = getOrCreateArtist(user);

        // Update Artist fields
        currentArtist.setArtistName(artist.getArtistName());
        currentArtist.setGenre(artist.getGenre());
        currentArtist.setWebsite(artist.getWebsite());
        currentArtist.setInstagram(artist.getInstagram());
        currentArtist.setTwitter(artist.getTwitter());
        currentArtist.setYoutube(artist.getYoutube());

        artistService.updateArtist(currentArtist);

        // Update User Profile Image (Artist's User)
        if (removeProfileImage) {
            user.setProfileImage(null);
            userService.updateUser(user);
        } else if (profileImageFile != null && !profileImageFile.isEmpty()) {
            user.setProfileImage(profileImageFile.getBytes());
            userService.updateUser(user);
        }
        return "redirect:/artist/settings";
    }

    @GetMapping("/songs/manage/{id}")
    public String manageSong(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        Song song = songService.getSongById(id);

        // Security check: only the owner can manage
        if (!song.getArtist().getArtistId().equals(artist.getArtistId())) {
            return "redirect:/artist/dashboard";
        }

        model.addAttribute("song", song);
        model.addAttribute("albums", albumService.getAlbumsByArtist(artist));
        return "artist/manage-song";
    }

    @PostMapping("/songs/update/{id}")
    public String updateSong(@PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Song song,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "albumId", required = false) Long albumId) throws IOException {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        Song existingSong = songService.getSongById(id);

        if (!existingSong.getArtist().getArtistId().equals(artist.getArtistId())) {
            return "redirect:/artist/dashboard";
        }

        existingSong.setTitle(song.getTitle());
        existingSong.setGenre(song.getGenre());

        if (file != null && !file.isEmpty()) {
            existingSong.setAudioFile(file.getBytes());
        }

        if (cover != null && !cover.isEmpty()) {
            existingSong.setCoverImage(cover.getBytes());
        }

        if (albumId != null) {
            Album album = albumService.getAlbumById(albumId);
            existingSong.setAlbum(album);
        } else {
            existingSong.setAlbum(null);
        }

        songService.updateSong(existingSong);
        return "redirect:/artist/dashboard";
    }

    @GetMapping("/songs/delete/{id}")
    public String deleteSong(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        try {
            Song song = songService.getSongById(id);
            if (song.getArtist().getArtistId().equals(artist.getArtistId())) {
                songService.deleteSong(id);
            }
        } catch (com.revature.revplay.customexceptions.SongNotFoundException e) {
            // Already deleted
        }

        return "redirect:/artist/dashboard";
    }

    @GetMapping("/albums/manage/{id}")
    public String manageAlbum(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        Album album = albumService.getAlbumById(id);

        if (!album.getArtist().getArtistId().equals(artist.getArtistId())) {
            return "redirect:/artist/dashboard";
        }

        model.addAttribute("album", album);
        return "artist/manage-album";
    }

    @PostMapping("/albums/update/{id}")
    public String updateAlbum(@PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Album album,
            @RequestParam(value = "cover", required = false) MultipartFile cover) throws IOException {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        Album existingAlbum = albumService.getAlbumById(id);

        if (!existingAlbum.getArtist().getArtistId().equals(artist.getArtistId())) {
            return "redirect:/artist/dashboard";
        }

        existingAlbum.setAlbumName(album.getAlbumName());

        if (cover != null && !cover.isEmpty()) {
            existingAlbum.setCoverImage(cover.getBytes());
        }

        albumService.updateAlbum(existingAlbum);
        return "redirect:/artist/dashboard";
    }

    @GetMapping("/albums/delete/{id}")
    public String deleteAlbum(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Artist artist = getOrCreateArtist(user);
        try {
            Album album = albumService.getAlbumById(id);
            if (album.getArtist().getArtistId().equals(artist.getArtistId())) {
                albumService.deleteAlbum(id);
            }
        } catch (com.revature.revplay.customexceptions.AlbumNotFoundException e) {
            // Already deleted or not authorized
        }

        return "redirect:/artist/dashboard";
    }

    private Artist getOrCreateArtist(User user) {
        try {
            return artistService.getArtistByUser(user);
        } catch (ArtistNotFoundException e) {
            Artist artist = new Artist();
            artist.setUser(user);
            artist.setArtistName(
                    user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName()
                            : user.getUsername());
            return artistService.registerArtist(artist);
        }
    }
}
