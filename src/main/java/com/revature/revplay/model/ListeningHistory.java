package com.revature.revplay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "listening_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListeningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lh_seq_gen")
    @SequenceGenerator(name = "lh_seq_gen", sequenceName = "history_seq", allocationSize = 1)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    @Column(name = "listened_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime listenedAt;
}
