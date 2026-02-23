package com.revature.revplay.service.impl;

import com.revature.revplay.model.Artist;
import com.revature.revplay.model.User;
import com.revature.revplay.repository.ArtistRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.ProfileManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileManagementServiceImpl implements ProfileManagementService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    public ProfileManagementServiceImpl(UserRepository userRepository, ArtistRepository artistRepository) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional
    public User updateUserProfile(Long userId, String displayName, String bio, MultipartFile profileImage) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDisplayName(displayName);
        user.setBio(bio);

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                user.setProfileImage(profileImage.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to store profile image", e);
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Artist updateArtistProfile(Long userId, String artistName, String bio, String genre,
            MultipartFile bannerImage) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist record not found"));

        artist.setArtistName(artistName);
        artist.setGenre(genre);
        user.setDisplayName(artistName);
        user.setBio(bio);

        if (bannerImage != null && !bannerImage.isEmpty()) {
            try {
                artist.setBannerImage(bannerImage.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to store banner image", e);
            }
        }

        userRepository.save(user);
        return artistRepository.save(artist);
    }

    @Override
    public Artist getArtistByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return artistRepository.findByUser(user).orElse(null);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
