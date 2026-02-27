package com.revature.revplay.service;

import com.revature.revplay.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SongService {

    void createSong(User user,
                    String title,
                    Genre genre,
                    Integer duration,
                    String releaseDate,
                    Visibility visibility,
                    Long albumId,
                    MultipartFile audioFile) throws IOException;

    byte[] getSongAudio(Long id);

    Song getSongById(Long id);

    List<Album> getAlbumsByArtist(User user);

    void updateSong(User user,
                    Long songId,
                    String title,
                    Genre genre,
                    Integer duration,
                    String releaseDate,
                    Visibility visibility,
                    Long albumId,
                    MultipartFile audioFile) throws IOException;

    void deleteSong(Long id, User user);
}
