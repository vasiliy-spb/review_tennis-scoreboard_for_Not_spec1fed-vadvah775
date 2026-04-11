package com.tennisscoreboard.service;

import com.tennisscoreboard.model.Match;
import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.model.Player;
import com.tennisscoreboard.repository.MatchRepository;

public class MatchScoreService {
    private final MatchRepository matchRepository = new MatchRepository();

    int winnerPoints;
    int loserPoints;
    int winnerGames;
    int loserGames;
    int winnerSets;
    int loserSets;

    private void setScore(OngoingMatch match, Long winnerId) {
        Player player1 = match.getPlayer1();
        Player player2 = match.getPlayer2();

        if (player1.getId().equals(winnerId)) {
            winnerPoints = match.getPointsPlayer1();
            loserPoints = match.getPointsPlayer2();
            winnerGames = match.getGamesPlayer1();
            loserGames = match.getGamesPlayer2();
            winnerSets = match.getSetsPlayer1();
            loserSets = match.getSetsPlayer2();
        } else if(player2.getId().equals(winnerId)){
            winnerPoints = match.getPointsPlayer2();
            loserPoints = match.getPointsPlayer1();
            winnerGames = match.getGamesPlayer2();
            loserGames = match.getGamesPlayer1();
            winnerSets = match.getSetsPlayer2();
            loserSets = match.getSetsPlayer1();
        } else {
            throw new IllegalArgumentException("winnerId doesn't belong to the match");
        }
    }

    private void savingScore(OngoingMatch match, Long winnerId) {
        Player player1 = match.getPlayer1();

        if (player1.getId().equals(winnerId)) {
            match.setPointsPlayer1(winnerPoints);
            match.setGamesPlayer1(winnerGames);
            match.setSetsPlayer1(winnerSets);
            match.setPointsPlayer2(loserPoints);
            match.setGamesPlayer2(loserGames);
            match.setSetsPlayer2(loserSets);
        } else {
            match.setPointsPlayer2(winnerPoints);
            match.setGamesPlayer2(winnerGames);
            match.setSetsPlayer2(winnerSets);
            match.setPointsPlayer1(loserPoints);
            match.setGamesPlayer1(loserGames);
            match.setSetsPlayer1(loserSets);
        }
    }

    public void addPoint(OngoingMatch match, Long winnerId) {
        setScore(match, winnerId);

        if (match.isTiebreak()) {
            winnerPoints++;
            if (winnerPoints >= 6 && winnerPoints - loserPoints >= 2) {
                winGame(match, winnerId);
            }
        }else{
            switch (winnerPoints) {
                case 0: winnerPoints = 1; break;
                case 1: winnerPoints = 2; break;
                case 2: winnerPoints = 3; break;
                case 3:
                    if (loserPoints <= 2){
                        winGame(match, winnerId);
                    } else if (loserPoints == 3) { // deuce -> advantage
                        winnerPoints = 4;
                    } else if (loserPoints == 4){ //advantage
                        loserPoints = 3;
                    }
                    break;
                case 4: // advantage -> winSet
                    winGame(match, winnerId);
                    break;
            }
        }

        savingScore(match, winnerId);
    }

    private void winGame(OngoingMatch match, Long winnerId) {
        winnerPoints = 0;
        loserPoints = 0;

        winnerGames++;

        if (match.isTiebreak()) {
            winSet(match, winnerId);
        }

        if(winnerGames >= 6 && winnerGames - loserGames >= 2){
            winSet(match, winnerId);
        } else if (winnerGames == 6 && loserGames == 6) {
            match.setTiebreak(true);
        }
    }

    private void winSet(OngoingMatch match, Long winnerId){
        winnerGames = 0;
        loserGames = 0;

        match.setTiebreak(false);

        winnerSets++;

        if(winnerSets == 2){
            match.setFinished(true);
            savingMatchInDB(match, winnerId);
        }
    }

    private void savingMatchInDB(OngoingMatch ongoingMatch, Long winnerId){
        Match match = new Match();
        Player player1 = ongoingMatch.getPlayer1();
        Player player2 = ongoingMatch.getPlayer2();

        match.setPlayer1(player1);
        match.setPlayer2(player2);

        if (player1.getId().equals(winnerId)) {
            match.setWinner(player1);
        } else {
            match.setWinner(player2);
        }

        matchRepository.save(match);
    }
}
