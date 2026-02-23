package com.revature.revplay.service.impl;

import com.revature.revplay.model.Artist;
import com.revature.revplay.model.Role;
import com.revature.revplay.model.User;
import com.revature.revplay.repository.ArtistRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.UserIdentityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserIdentityServiceImpl implements UserIdentityService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final PasswordEncoder passwordEncoder;

    public UserIdentityServiceImpl(UserRepository userRepository, ArtistRepository artistRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerSubscriber(String email, String username, String password, String displayName) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerArtist(String email, String username, String password, String artistName, String genre) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(artistName);
        user.setRole(Role.ARTIST);
        User savedUser = userRepository.save(user);

        Artist artist = new Artist();
        artist.setUser(savedUser);
        artist.setArtistName(artistName);
        artist.setGenre(genre);
        artistRepository.save(artist);

        return savedUser;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
