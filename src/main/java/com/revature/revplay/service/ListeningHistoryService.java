package com.revature.revplay.service;

import com.revature.revplay.model.ListeningHistory;
import com.revature.revplay.model.User;
import com.revature.revplay.model.Song;
import java.util.List;

public interface ListeningHistoryService {
    void addToHistory(User user, Song song);

    List<ListeningHistory> getHistoryByUser(User user);

    void clearHistory(User user);
}
