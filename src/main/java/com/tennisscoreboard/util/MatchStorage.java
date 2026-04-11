package com.tennisscoreboard.util;

import com.tennisscoreboard.model.OngoingMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchStorage {
    private static final Map<String, OngoingMatch> matches = new HashMap<>();

    public static void put(OngoingMatch match) {
        matches.put(match.getUUID(), match);
    }

    public static OngoingMatch get(String uuid) {
        return matches.get(uuid);
    }

    public static List<OngoingMatch> getAll(){
        return new ArrayList<>(matches.values());
    }

    public static void remove(String uuid) {
        matches.remove(uuid);
    }
}
