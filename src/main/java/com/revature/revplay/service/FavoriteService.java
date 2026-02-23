package com.revature.revplay.service;

import com.revature.revplay.model.Favorite;
import com.revature.revplay.model.User;
import com.revature.revplay.model.Song;
import java.util.List;

public interface FavoriteService {
    void addToFavorites(User user, Song song);

    void removeFromFavorites(User user, Song song);

    List<Favorite> getFavoritesByUser(User user);

    boolean isFavorite(User user, Song song);

    void deleteFavorite(Long id);
}
