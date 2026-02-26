package com.revature.revplay.service;

import com.revature.revplay.model.User;

public interface UserIdentityService {
    User registerSubscriber(String email, String username, String password, String displayName);

    User registerArtist(String email, String username, String password, String artistName, String genre);

    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void resetPassword(String email, String username, String newPassword);
}
