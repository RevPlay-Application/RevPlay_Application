package com.revature.revplay.service.impl;

import com.revature.revplay.model.Favorite;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.User;
import com.revature.revplay.repository.FavoriteRepository;
import com.revature.revplay.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public void addToFavorites(User user, Song song) {
        if (!favoriteRepository.existsByUserAndSong(user, song)) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setSong(song);
            favoriteRepository.save(favorite);
        }
    }

    @Override
    @Transactional
    public void removeFromFavorites(User user, Song song) {
        favoriteRepository.deleteByUserAndSong(user, song);
    }

    @Override
    public List<Favorite> getFavoritesByUser(User user) {
        return favoriteRepository.findByUser(user);
    }

    @Override
    public boolean isFavorite(User user, Song song) {
        return favoriteRepository.existsByUserAndSong(user, song);
    }

    @Override
    @Transactional
    public void deleteFavorite(Long id) {
        favoriteRepository.deleteById(id);
    }
}
