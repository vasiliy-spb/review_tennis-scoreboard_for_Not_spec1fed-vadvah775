package com.tennisscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OngoingMatch {
    private final String UUID;
    private final Player player1;
    private final Player player2;

    private int setsPlayer1;
    private int setsPlayer2;

    private int gamesPlayer1;
    private int gamesPlayer2;

    private int pointsPlayer1;
    private int pointsPlayer2;

    private boolean finished = false;
    private boolean tiebreak = false;

    public OngoingMatch(Player player1, Player player2) {
        UUID = java.util.UUID.randomUUID().toString();
        this.player1 = player1;
        this.player2 = player2;

    }
}
