package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface PlayerService {

    List<Player> getFilteredAndSortedPlayersList(String name,
                                String title,
                                Race race,
                                Profession profession,
                                Long after,
                                Long before,
                                Boolean banned,
                                Integer minExperience,
                                Integer maxExperience,
                                Integer minLevel,
                                Integer maxLevel,
                                PlayerOrder playerOrder);

    Integer getPlayersCount(String name, String title, Race race, Profession profession, Long after,
                            Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                            Integer minLevel, Integer maxLevel);

    List<Player> getPagedPlayersList(List<Player> players, int pageNumber, int pageSize);

    Player createPlayer(String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience);

    Player savePlayer(Player player);

    Player getPlayer(Long id);

    ResponseEntity<Player> updatePlayer(Long id, String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience);

    void deletePlayer (Player player);
}
