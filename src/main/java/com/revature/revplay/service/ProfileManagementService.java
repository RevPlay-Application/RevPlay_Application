package com.revature.revplay.service;

import com.revature.revplay.model.Artist;
import com.revature.revplay.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileManagementService {
    User updateUserProfile(Long userId, String displayName, String bio, MultipartFile profileImage);

    Artist updateArtistProfile(Long userId, String artistName, String bio, String genre, MultipartFile bannerImage);

    Artist getArtistByUserId(Long userId);
}
