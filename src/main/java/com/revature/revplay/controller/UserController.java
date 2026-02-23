package com.revature.revplay.controller;

import com.revature.revplay.model.*;
import com.revature.revplay.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SongService songService;
    private final FavoriteService favoriteService;
    private final PlaylistService playlistService;
    private final ListeningHistoryService historyService;

    @GetMapping("/favorites")
    public String viewFavorites(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<Favorite> favorites = favoriteService.getFavoritesByUser(user);
        model.addAttribute("favorites", favorites);
        return "user/favorites";
    }

    @PostMapping("/favorites/toggle/{songId}")
    @ResponseBody
    public java.util.Map<String, Object> toggleFavorite(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long songId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Song song = songService.getSongById(songId);
        boolean isFav = favoriteService.isFavorite(user, song);
        if (isFav) {
            favoriteService.removeFromFavorites(user, song);
        } else {
            favoriteService.addToFavorites(user, song);
        }
        return java.util.Map.of("isFavorite", !isFav);
    }

    @GetMapping("/favorites/check/{songId}")
    @ResponseBody
    public java.util.Map<String, Object> checkFavorite(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long songId) {
        if (userDetails == null)
            return java.util.Map.of("isFavorite", false);
        User user = userService.getUserByUsername(userDetails.getUsername());
        Song song = songService.getSongById(songId);
        return java.util.Map.of("isFavorite", favoriteService.isFavorite(user, song));
    }

    @GetMapping("/playlists")
    public String viewPlaylists(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<Playlist> playlists = playlistService.getPlaylistsByUser(user);
        model.addAttribute("playlists", playlists);
        return "user/playlists";
    }

    @PostMapping("/playlists/create")
    public String createPlaylist(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("name") String name) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setUser(user);
        playlist.setPrivacy(Privacy.PRIVATE);
        playlistService.createPlaylist(playlist);
        return "redirect:/user/playlists";
    }

    @GetMapping("/playlists/{id}")
    public String viewPlaylist(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Playlist playlist = playlistService.getPlaylistById(id);

        if (!playlist.getUser().getUserId().equals(user.getUserId())) {
            return "redirect:/user/playlists";
        }

        model.addAttribute("playlist", playlist);
        return "user/view-playlist";
    }

    @PostMapping("/playlists/update/{id}")
    public String updatePlaylist(@PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Playlist playlist = playlistService.getPlaylistById(id);

        if (!playlist.getUser().getUserId().equals(user.getUserId())) {
            return "redirect:/user/playlists";
        }

        playlist.setName(name);
        playlist.setDescription(description);
        playlistService.updatePlaylist(playlist);
        return "redirect:/user/playlists/" + id;
    }

    @GetMapping("/playlists/delete/{id}")
    public String deletePlaylist(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        try {
            Playlist playlist = playlistService.getPlaylistById(id);
            if (playlist.getUser().getUserId().equals(user.getUserId())) {
                playlistService.deletePlaylist(id);
            }
        } catch (com.revature.revplay.customexceptions.PlaylistNotFoundException e) {
            // Already deleted
        }
        return "redirect:/user/playlists";
    }

    @PostMapping("/playlists/{playlistId}/remove/{songId}")
    public String removeSongFromPlaylist(@PathVariable("playlistId") Long playlistId,
            @PathVariable("songId") Long songId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Playlist playlist = playlistService.getPlaylistById(playlistId);

        if (playlist.getUser().getUserId().equals(user.getUserId())) {
            playlistService.removeSongFromPlaylist(playlistId, songId);
        }
        return "redirect:/user/playlists/" + playlistId;
    }

    @PostMapping("/favorites/remove/{id}")
    public String removeFavorite(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            User user = userService.getUserByUsername(userDetails.getUsername());
            List<Favorite> favorites = favoriteService.getFavoritesByUser(user);
            boolean pwned = favorites.stream().anyMatch(f -> f.getFavoriteId().equals(id));
            if (pwned) {
                favoriteService.deleteFavorite(id);
            }
        }
        return "redirect:/user/favorites";
    }

    @PostMapping("/playlists/add")
    @ResponseBody
    public java.util.Map<String, Object> addSongToPlaylist(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long playlistId, @RequestParam Long songId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Playlist playlist = playlistService.getPlaylistById(playlistId);

        if (playlist.getUser().getUserId().equals(user.getUserId())) {
            playlistService.addSongToPlaylist(playlistId, songId);
            return java.util.Map.of("success", true);
        }
        return java.util.Map.of("success", false, "message", "Unauthorized");
    }

    @GetMapping("/playlists/json")
    @ResponseBody
    public List<java.util.Map<String, Object>> getUserPlaylistsJson(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return List.of();
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<Playlist> playlists = playlistService.getPlaylistsByUser(user);
        return playlists.stream().map(p -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", p.getPlaylistId());
            map.put("name", p.getName() != null ? p.getName() : "Untitled");
            return map;
        }).toList();
    }

    @GetMapping("/history")
    public String viewHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<ListeningHistory> history = historyService.getHistoryByUser(user);
        model.addAttribute("history", history);
        return "user/history";
    }

    @PostMapping("/history/clear")
    @ResponseBody
    public java.util.Map<String, Object> clearHistory(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        historyService.clearHistory(user);
        return java.util.Map.of("success", true);
    }

    @GetMapping("/profile-image/{userId}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        byte[] image = user.getProfileImage();
        if (image != null && image.length > 0) {
            return org.springframework.http.ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.IMAGE_JPEG)
                    .body(image);
        } else {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
    }
}
