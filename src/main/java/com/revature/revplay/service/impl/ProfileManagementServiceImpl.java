package com.revature.revplay.service.impl;

import com.revature.revplay.customexceptions.InvalidFileException;
import com.revature.revplay.customexceptions.ResourceNotFoundException;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setDisplayName(displayName);
        user.setBio(bio);

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                user.setProfileImage(profileImage.getBytes());
            } catch (IOException e) {
                throw new InvalidFileException("Failed to store profile image: " + e.getMessage());
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Artist updateArtistProfile(Long userId, String artistName, String bio, String genre,
            String instagram, String twitter, String youtube, String website,
            MultipartFile bannerImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Artist record not found for user: " + user.getUsername()));

        artist.setArtistName(artistName);
        artist.setGenre(genre);
        artist.setInstagram(instagram);
        artist.setTwitter(twitter);
        artist.setYoutube(youtube);
        artist.setWebsite(website);
        user.setDisplayName(artistName);
        user.setBio(bio);

        if (bannerImage != null && !bannerImage.isEmpty()) {
            try {
                artist.setBannerImage(bannerImage.getBytes());
            } catch (IOException e) {
                throw new InvalidFileException("Failed to store banner image: " + e.getMessage());
            }
        }

        userRepository.save(user);
        return artistRepository.save(artist);
    }

    @Override
    public Artist getArtistByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return artistRepository.findByUser(user).orElse(null);
    }
}
