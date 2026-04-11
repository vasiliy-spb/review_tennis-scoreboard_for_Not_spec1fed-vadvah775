package com.service;

import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.model.Player;
import com.tennisscoreboard.service.MatchScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatchScoreServiceTest {

    private Player player1;
    private Player player2;
    private OngoingMatch match;
    private MatchScoreService service;

    @BeforeEach
    void setUp(){
        player1 = new Player("Bob");
        player1.setId(1L);
        player2 = new Player("Alice");
        player2.setId(2L);
        match = new OngoingMatch(player1, player2);
        service = new MatchScoreService();
    }

    private void reachDeuce(){
        for (int i = 0; i < 3; ++i) {
            service.addPoint(match, player1.getId());
            service.addPoint(match, player2.getId());
        }
    }

    private void winGame(Long winnerId){
        for(int i = 0; i < 4; ++i){
            service.addPoint(match, winnerId);
        }
    }


    @Test
    @DisplayName("points: 0 -> 15 -> 30 -> 40")
    void regularPointsSequence(){
        // 0-0 -> 15-0
        service.addPoint(match, player1.getId());
        assertEquals(1, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());

        // 15-0 -> 30-0
        service.addPoint(match, player1.getId());
        assertEquals(2, match.getPointsPlayer1());

        // 30-0 -> 40-0
        service.addPoint(match, player1.getId());
        assertEquals(3, match.getPointsPlayer1());
    }

    @Test
    @DisplayName("40-0: add point and win game")
    void winGameAt40_0(){
        for (int i = 0; i < 3; ++i) service.addPoint(match, player1.getId());

        assertFalse(match.isFinished());
        assertEquals(3, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());

        assertEquals(0, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());

        service.addPoint(match, player1.getId());

        assertEquals(1, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());
        assertEquals(0, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());
        assertFalse(match.isFinished());
    }

    @Test
    @DisplayName("Deuce: winning a point gives an advantage, the game does not end")
    void deuceAdvantage(){
        reachDeuce();

        assertEquals(3, match.getPointsPlayer1());
        assertEquals(3, match.getPointsPlayer2());

        service.addPoint(match, player1.getId());

        assertEquals(4, match.getPointsPlayer1());
        assertEquals(3, match.getPointsPlayer2());
        assertEquals(0, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());
    }

    @Test
    @DisplayName("Deuce -> Advantage -> Win game")
    void advantageThenWinGame(){
        reachDeuce();
        service.addPoint(match, player1.getId());
        service.addPoint(match, player1.getId());

        assertEquals(0, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());
        assertEquals(1, match.getGamesPlayer1());
    }

    @Test
    @DisplayName("Deuce -> Advantage for player1 -> adding point to player1 -> Deuce")
    void loseAdvantageBackToAdvantage(){
        reachDeuce();
        service.addPoint(match, player1.getId());
        assertEquals(4, match.getPointsPlayer1());
        assertEquals(3, match.getPointsPlayer2());

        service.addPoint(match, player2.getId());

        assertEquals(3, match.getPointsPlayer1());
        assertEquals(3, match.getPointsPlayer2());

        assertEquals(0, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());
    }

    @Test
    @DisplayName("Win set at 6-0")
    void winSet6_0(){
        for(int i = 0; i < 6; ++i){
            winGame(player1.getId());
        }

        assertEquals(1, match.getSetsPlayer1());
        assertEquals(0, match.getSetsPlayer2());

        assertEquals(0, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());

        assertEquals(0, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());

        assertFalse(match.isFinished());
    }


    @Test
    @DisplayName("tiebreak starts at 6-6 games")
    void tiebreakStartsAt6_6() {
        for(int i = 0; i < 6; ++i){
            winGame(player1.getId());
            winGame(player2.getId());
        }

        assertEquals(6, match.getGamesPlayer1());
        assertEquals(6, match.getGamesPlayer2());

        assertTrue(match.isTiebreak());
    }

    @Test
    @DisplayName("Win set at 7-5")
    void winSetAt7_5(){
        for(int i = 0; i < 5; ++i){
            winGame(player1.getId());
            winGame(player2.getId());
        }

        winGame(player1.getId());

        assertEquals(6, match.getGamesPlayer1());
        assertEquals(5, match.getGamesPlayer2());

        assertFalse(match.isTiebreak());

        winGame(player1.getId());

        assertEquals(1, match.getSetsPlayer1());
        assertEquals(0, match.getSetsPlayer2());

        assertEquals(0, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());
        assertEquals(0, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());

        assertFalse(match.isTiebreak());
    }

    @Test
    @DisplayName("Tiebreak starts when game score 6-6")
    void tiebreakStartAt6_6Game(){
        for(int i = 0; i < 6; ++i){
            winGame(player1.getId());
            winGame(player2.getId());
        }

        assertEquals(6, match.getGamesPlayer1());
        assertEquals(6, match.getGamesPlayer2());

        assertTrue(match.isTiebreak());
    }

    @Test
    @DisplayName("Win tiebreak 6-0")
    void winTiebreakAt6_0(){
        for(int i = 0; i < 6; ++i){
            winGame(player1.getId());
            winGame(player2.getId());
        }

        assertTrue(match.isTiebreak());

        for (int i = 0; i < 5; i++) {
            service.addPoint(match, player1.getId());
        }
        assertEquals(5, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());
        assertTrue(match.isTiebreak());

        service.addPoint(match, player1.getId());

        assertEquals(1, match.getSetsPlayer1());
        assertEquals(0, match.getSetsPlayer2());
        assertEquals(0, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());
        assertEquals(0, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());
    }

    @Test
    @DisplayName("Win tiebreak 9_7")
    void winTiebreakAt9_7(){
        for(int i = 0; i < 6; ++i){
            winGame(player1.getId());
            winGame(player2.getId());
        }

        assertTrue(match.isTiebreak());

        for (int i = 0; i < 5; i++) {
            service.addPoint(match, player1.getId());
            service.addPoint(match, player2.getId());
        }

        assertEquals(5, match.getPointsPlayer1());
        assertEquals(5, match.getPointsPlayer2());

        for(int i = 0; i < 2; ++i){
            service.addPoint(match, player1.getId());
            service.addPoint(match, player2.getId());
        } // 7-7 points

        assertEquals(7, match.getPointsPlayer1());
        assertEquals(7, match.getPointsPlayer2());

        service.addPoint(match, player1.getId());
        service.addPoint(match, player1.getId());

        assertEquals(1, match.getSetsPlayer1());
        assertEquals(0, match.getSetsPlayer2());
        assertEquals(0, match.getGamesPlayer1());
        assertEquals(0, match.getGamesPlayer2());
        assertEquals(0, match.getPointsPlayer1());
        assertEquals(0, match.getPointsPlayer2());
    }
}
