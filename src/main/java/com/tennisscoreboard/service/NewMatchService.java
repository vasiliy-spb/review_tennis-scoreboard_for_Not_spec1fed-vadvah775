package com.tennisscoreboard.service;

import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.model.Player;
import com.tennisscoreboard.repository.PlayerRepository;
import com.tennisscoreboard.util.MatchStorage;

import java.util.List;

public class NewMatchService {
    private final PlayerRepository playerRepository = new PlayerRepository();

    public String createNewMatch(String playerOneName, String playerTwoName){
        Player player1 = playerRepository.getOrCreatePlayer(playerOneName);
        Player player2 = playerRepository.getOrCreatePlayer(playerTwoName);

        OngoingMatch match = new OngoingMatch(player1, player2);
        MatchStorage.put(match);

        return match.getUUID();
    }

    public boolean checkPlayersBusy(String playerOneName, String playerTwoName){
        List<OngoingMatch> list = MatchStorage.getAll();
        for (OngoingMatch match : list) {
            if(match.getPlayer1().getName().equals(playerOneName)) return true;
            if(match.getPlayer1().getName().equals(playerTwoName)) return true;
            if(match.getPlayer2().getName().equals(playerOneName)) return true;
            if(match.getPlayer2().getName().equals(playerTwoName)) return true;
        }
        return false;
    }

}
