package com.revature.revplay.controller;

import com.revature.revplay.entity.Album;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumViewController {
    
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    
    @GetMapping("/{id}")
    public String viewAlbumDetail(@PathVariable Long id, Model model) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        
        model.addAttribute("album", album);
        model.addAttribute("songs", songRepository.findByAlbumId(id));
        
        return "album/detail";
    }
}
