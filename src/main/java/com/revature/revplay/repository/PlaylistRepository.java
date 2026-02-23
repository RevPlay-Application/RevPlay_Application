package com.revature.revplay.repository;

import com.revature.revplay.model.Playlist;
import com.revature.revplay.model.Privacy;
import com.revature.revplay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUser(User user);

    List<Playlist> findByPrivacy(Privacy privacy);

    List<Playlist> findByNameContainingIgnoreCaseAndPrivacy(String name, Privacy privacy);
}
