package com.revature.revplay.controller;

import com.revature.revplay.model.Album;
import com.revature.revplay.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/cover/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getAlbumCover(@PathVariable Long id) {
        Album album = albumService.getAlbumById(id);
        if (album.getCoverImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Assuming JPEG, or detect type if stored
                .body(album.getCoverImage());
    }
}
