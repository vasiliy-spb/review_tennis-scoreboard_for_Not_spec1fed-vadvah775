package com.tennisscoreboard.repository;

import com.tennisscoreboard.model.Player;

import java.util.Optional;

public class PlayerRepository extends BaseRepository<Player, Long> {

    // TODO: Нет интерфейса для этого класса. (см. файл "repository.md" в этом же пакете)

    public PlayerRepository(){
        super(Player.class);
    }

    public Optional<Player> findByName(String name){
        return executeReadOnly(session -> {

            // Текст HQL запроса удобнее читать, когда он логично разбит на строки, даже если он короткий.
                // Для визуального разделения запросов на строки лучше использовать текстовые блоки

            // Лучше вынести текст HQL запроса в `private static final` константу и дать ей понятное имя.
            String hql = "FROM Player WHERE name = :name";
            return session.createQuery(hql, Player.class)

                    // Название параметра "name" тоже лучше вынести в именованную константу
                    .setParameter("name", name)
                    .uniqueResultOptional();
        });
    }

    // TODO: DAO слой должен заниматься доступом к данным, а не бизнес-логикой. Логика найти или создать должна быть в сервисном слое.
        // (см. файл "Принцип разделения ответственности (Separation of Concerns).md" в этом же пакете)
    public Player getOrCreatePlayer(String name){
        Optional<Player> player = findByName(name);
        if(player.isPresent()){
            return player.get();
        } else{
            Player newPlayer = new Player(name);
            save(newPlayer);
            return newPlayer;
        }
    }
}
