package com.tennisscoreboard.service;

import com.tennisscoreboard.model.Match;
import com.tennisscoreboard.repository.MatchRepository;

import java.util.List;

public class MatchesService {
    private final MatchRepository matchRepository = new MatchRepository();
    private final static int PAGE_SIZE = 5;

    public List<Match> getMatchesPage(int page, String filterName) {
        int offset = (page - 1) * PAGE_SIZE;
        return matchRepository.findMatchesWithPaginationAndFilter(offset, PAGE_SIZE, filterName);
    }

    public long getTotalMatchesCount(String filterName){
        return matchRepository.countMatchesWithFilter(filterName);
    }

    public int getTotalPages(String filterName){
        long totalCount = getTotalMatchesCount(filterName);
        return (int) Math.ceil((double) totalCount / PAGE_SIZE);
    }
}
