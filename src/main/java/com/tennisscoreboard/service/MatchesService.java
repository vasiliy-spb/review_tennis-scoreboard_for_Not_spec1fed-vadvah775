package com.tennisscoreboard.service;

import com.tennisscoreboard.model.Match;
import com.tennisscoreboard.repository.MatchRepository;

import java.util.List;

public class MatchesService {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: MatchRepository стоит внедрять через конструктор, а не создавать в этом классе.

    private final MatchRepository matchRepository = new MatchRepository();

    // Константы нужно объявлять первыми (самыми верхними) в классе
    // Размер страницы по умолчанию более уместно хранить в сервлете, так как в идеале он должен приходить с фронтенда.
        // А сервис должен принимать это значение в качестве аргумента в методы.
    private final static int PAGE_SIZE = 5;

    // Метод передаёт JPA-сущности в контроллер. Это нарушение принципа разделения ответственности между слоями.
        // Сервисный слой должен служить границей, которая изолирует доменную логику
        // и персистентность (слой долговременного хранения данных) от внешнего мира (например, от контроллеров).
        // Метод должен возвращать список DTO.
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
