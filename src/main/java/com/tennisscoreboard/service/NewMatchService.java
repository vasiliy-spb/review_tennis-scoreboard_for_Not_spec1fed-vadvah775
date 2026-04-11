package com.tennisscoreboard.service;

import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.model.Player;
import com.tennisscoreboard.repository.PlayerRepository;
import com.tennisscoreboard.util.MatchStorage;

import java.util.List;
import java.util.regex.Pattern;

public class NewMatchService {
    private final PlayerRepository playerRepository = new PlayerRepository();

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.-]+$");

    public String createNewMatch(String playerOneName, String playerTwoName){
        validatePlayerName(playerOneName);
        validatePlayerName(playerTwoName);

        if (playerOneName.trim().equalsIgnoreCase(playerTwoName.trim())) {
            throw new IllegalArgumentException("Player names must be different");
        }

        if (checkPlayersBusy(playerOneName, playerTwoName)) {
            throw new IllegalArgumentException("This player is already busy");
        }

        Player player1 = playerRepository.getOrCreatePlayer(playerOneName);
        Player player2 = playerRepository.getOrCreatePlayer(playerTwoName);

        OngoingMatch match = new OngoingMatch(player1, player2);
        MatchStorage.put(match);

        return match.getUUID();
    }

    private void validatePlayerName(String name){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        String trimmed = name.trim();
        if (trimmed.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must be at least " + MIN_NAME_LENGTH + " characters");
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Name contains invalid characters. Only letters, digits, spaces, dots and hyphens are allowed.");
        }
    }

    private boolean checkPlayersBusy(String playerOneName, String playerTwoName){
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
