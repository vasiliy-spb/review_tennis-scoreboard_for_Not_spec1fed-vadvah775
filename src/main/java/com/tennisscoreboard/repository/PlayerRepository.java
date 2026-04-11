package com.tennisscoreboard.repository;

import com.tennisscoreboard.model.Player;

import java.util.Optional;

public class PlayerRepository extends BaseRepository<Player, Long> {


    public PlayerRepository(){
        super(Player.class);
    }

    public Optional<Player> findByName(String name){
        return executeReadOnly(session -> {
            String hql = "FROM Player WHERE name = :name";
            return session.createQuery(hql, Player.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();
        });
    }

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
