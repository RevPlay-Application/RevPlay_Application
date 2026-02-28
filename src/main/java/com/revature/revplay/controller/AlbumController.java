package com.revature.revplay.controller;

import com.revature.revplay.model.*;
import com.revature.revplay.service.AlbumService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/artist/album")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping("/create")
    public String createAlbum(@ModelAttribute("authenticatedUser") User user,
                              @RequestParam String albumName,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Genre genre,
                              @RequestParam(required = false) String releaseDate,
                              @RequestParam(required = false) MultipartFile coverImage)
            throws IOException {

        if (user == null) return "redirect:/login";

        albumService.createAlbum(user, albumName, description,
                genre, releaseDate, coverImage);

        return "redirect:/artist/dashboard?albumCreated=true";
    }

    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable Long id,
                            @ModelAttribute("authenticatedUser") User user,
                            Model model) {

        if (user == null) return "redirect:/login";

        model.addAttribute("album", albumService.getAlbumById(id));
        model.addAttribute("songs", albumService.getSongsByAlbum(id));

        return "artist/album-details";
    }

    @GetMapping("/cover/{id}")
    public ResponseEntity<byte[]> getAlbumCover(@PathVariable Long id) {

        byte[] cover = albumService.getAlbumCover(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(cover);
    }

    @PostMapping("/delete/{id}")
    public String deleteAlbum(@PathVariable Long id,
                              @ModelAttribute("authenticatedUser") User user) {

        if (user == null) return "redirect:/login";

        albumService.deleteAlbum(id, user);

        return "redirect:/artist/dashboard?albumDeleted=true";


    }

    @GetMapping("/edit/{id}")
    public String editAlbumPage(@PathVariable Long id,
                                @ModelAttribute("authenticatedUser") User user,
                                Model model) {

        if (user == null) {
            return "redirect:/login";
        }

        Album album = albumService.getAlbumById(id);

        model.addAttribute("album", album);

        return "artist/manage-album";
    }

    @PostMapping("/update")
    public String updateAlbum(@ModelAttribute("authenticatedUser") User user,
                              @RequestParam Long albumId,
                              @RequestParam String albumName,
                              @RequestParam(required = false) Genre genre,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) MultipartFile coverImage)
            throws IOException {

        if (user == null) return "redirect:/login";

        albumService.updateAlbum(albumId, albumName, genre, description, coverImage);

        return "redirect:/artist/dashboard?albumUpdated=true";
    }
}