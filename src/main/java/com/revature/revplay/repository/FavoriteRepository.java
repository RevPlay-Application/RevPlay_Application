package com.revature.revplay.repository;

import com.revature.revplay.model.Favorite;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndSong(User user, Song song);

    boolean existsByUserAndSong(User user, Song song);

    void deleteByUserAndSong(User user, Song song);
}
