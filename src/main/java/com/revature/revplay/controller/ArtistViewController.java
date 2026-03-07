package com.revature.revplay.controller;

import com.revature.revplay.entity.User;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistViewController {
    
    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    
    @GetMapping("/{username}")
    public String viewArtistProfile(@PathVariable String username, Model model) {
        User artist = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        
        ArtistProfile profile = artistProfileRepository.findById(artist.getId())
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));
        
        model.addAttribute("artist", artist);
        model.addAttribute("profile", profile);
        model.addAttribute("songs", songRepository.findByArtist(artist));
        model.addAttribute("albums", albumRepository.findByArtist(artist));
        model.addAttribute("followerCount", 0L);
        model.addAttribute("isFollowing", false);
        
        return "artist/public-profile";
    }
}
