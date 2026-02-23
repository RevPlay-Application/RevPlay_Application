package com.revature.revplay.repository;

import com.revature.revplay.model.Artist;
import com.revature.revplay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findByUser(User user);

    Optional<Artist> findByArtistName(String artistName);
}
