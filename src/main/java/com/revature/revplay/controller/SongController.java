package com.revature.revplay.controller;

import com.revature.revplay.model.Album;
import com.revature.revplay.model.Artist;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.User;
import com.revature.revplay.service.AlbumService;
import com.revature.revplay.service.ArtistService;
import com.revature.revplay.service.ListeningHistoryService;
import com.revature.revplay.service.SongService;
import com.revature.revplay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final UserService userService;
    private final ListeningHistoryService historyService;
    private final AlbumService albumService;
    private final ArtistService artistService;

    @GetMapping("/songs/stream/{id}")
    public ResponseEntity<Resource> streamSong(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Song song = songService.getSongById(id);

        // Track history if logged in
        if (userDetails != null) {
            User user = userService.getUserByUsername(userDetails.getUsername());
            historyService.addToHistory(user, song);
        }

        // Increment play count
        songService.incrementPlayCount(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + song.getTitle() + ".mp3\"")
                .body(new ByteArrayResource(song.getAudioFile()));
    }

    // Correcting the above body - it needs the actual bytes
    @GetMapping("/songs/play/{id}")
    public ResponseEntity<Resource> playSong(@PathVariable Long id) {
        Song song = songService.getSongById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(new ByteArrayResource(song.getAudioFile()));
    }

    @GetMapping("/songs/search")
    public String searchSongs(@RequestParam(value = "query", required = false) String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/";
        }
        List<Song> songResults = songService.searchSongs(query);
        List<Album> albumResults = albumService.searchAlbums(query);
        model.addAttribute("songs", songResults);
        model.addAttribute("albums", albumResults);
        model.addAttribute("query", query);
        return "search-results";
    }

    @GetMapping("/songs/random")
    @ResponseBody
    public ResponseEntity<?> getRandomSong() {
        Song s = songService.getRandomSong();
        if (s == null)
            return ResponseEntity.notFound().build();

        Map<String, Object> map = new java.util.HashMap<>();
        map.put("songId", s.getSongId());
        map.put("title", s.getTitle());
        map.put("artist", new java.util.HashMap<String, Object>() {
            {
                put("artistName", s.getArtist().getArtistName());
            }
        });
        if (s.getAlbum() != null) {
            map.put("album", new java.util.HashMap<String, Object>() {
                {
                    put("albumId", s.getAlbum().getAlbumId());
                }
            });
        }
        return ResponseEntity.ok(map);
    }

    @GetMapping("/album/{id}")
    @ResponseBody
    public List<Map<String, Object>> getSongsByAlbum(@PathVariable Long id) {
        Album album = new Album();
        album.setAlbumId(id);
        List<Song> songs = songService.getSongsByAlbum(album);
        return songs.stream().map(s -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getSongId());
            map.put("title", s.getTitle());
            map.put("artist", s.getArtist().getArtistName());
            map.put("duration", s.getDuration() != null ? s.getDuration() : 0);
            map.put("coverId", s.getAlbum() != null ? s.getAlbum().getAlbumId() : null);
            return map;
        }).toList();
    }

    @GetMapping("/songs/cover/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getSongCover(@PathVariable Long id) {
        try {
            Song song = songService.getSongById(id);
            byte[] image = song.getCoverImage();

            // Fallback to album cover if song cover is missing
            if (image == null || image.length == 0) {
                if (song.getAlbum() != null) {
                    // Force fetch album to avoid lazy initialization issues
                    Album album = albumService.getAlbumById(song.getAlbum().getAlbumId());
                    image = album.getCoverImage();
                }
            }

            if (image != null && image.length > 0) {
                // Return original bytes. Browser will detect content type from magic numbers
                // usually.
                // Using IMAGE_PNG as a backup, or better yet, not specifying if it can be
                // mixed.
                // But for now, let's stick to IMAGE_JPEG or IMAGE_PNG.
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG) // PNG is a safe middle ground
                        .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000") // Cache for 1 year
                        .body(image);
            }
        } catch (Exception e) {
            // Ignore and return not found
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/artist/songs/{id}")
    @ResponseBody
    public List<Map<String, Object>> getSongsByArtist(@PathVariable Long id) {
        Artist artist = artistService.getArtistById(id);
        List<Song> songs = songService.getSongsByArtist(artist);
        return songs.stream().map(s -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getSongId());
            map.put("title", s.getTitle());
            map.put("artist", s.getArtist().getArtistName());
            map.put("duration", s.getDuration() != null ? s.getDuration() : 0);
            map.put("coverId", s.getAlbum() != null ? s.getAlbum().getAlbumId() : null);
            return map;
        }).toList();
    }
}
