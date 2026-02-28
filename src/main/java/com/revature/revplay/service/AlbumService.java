package com.revature.revplay.service;

import com.revature.revplay.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AlbumService {

    void createAlbum(User user,
                     String albumName,
                     String description,
                     Genre genre,
                     String releaseDate,
                     MultipartFile coverImage) throws IOException;

    Album getAlbumById(Long id);

    void updateAlbum(Long albumId,
                     String albumName,
                     Genre genre,
                     String description,
                     MultipartFile coverImage) throws IOException;

    List<Song> getSongsByAlbum(Long albumId);

    byte[] getAlbumCover(Long id);

    void deleteAlbum(Long id, User user);
}
