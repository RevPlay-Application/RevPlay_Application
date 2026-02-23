package com.revature.revplay.repository;

import com.revature.revplay.model.ListeningHistory;
import com.revature.revplay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {
    List<ListeningHistory> findByUserOrderByListenedAtDesc(User user);

    void deleteByUser(User user);
}
