package com.revature.revplay.service.impl;

import com.revature.revplay.model.ListeningHistory;
import com.revature.revplay.model.Song;
import com.revature.revplay.model.User;
import com.revature.revplay.repository.ListeningHistoryRepository;
import com.revature.revplay.service.ListeningHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListeningHistoryServiceImpl implements ListeningHistoryService {

    private final ListeningHistoryRepository historyRepository;

    @Override
    @Transactional
    public void addToHistory(User user, Song song) {
        ListeningHistory history = new ListeningHistory();
        history.setUser(user);
        history.setSong(song);
        historyRepository.save(history);
    }

    @Override
    public List<ListeningHistory> getHistoryByUser(User user) {
        return historyRepository.findByUserOrderByListenedAtDesc(user);
    }

    @Override
    @Transactional
    public void clearHistory(User user) {
        historyRepository.deleteByUser(user);
    }
}
