package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class PlayerServiceRepositoryImp implements PlayerService {

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceRepositoryImp(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Player> getFilteredAndSortedPlayersList(String name, String title, Race race, Profession profession, Long after,
                                                        Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                                        Integer minLevel, Integer maxLevel, PlayerOrder playerOrder
    ) {
        List<Player> players = playerRepository.findAll(Sort.by(Sort.Direction.ASC,playerOrder.getFieldName()));
        if (title!=null){
            players = players.stream().filter(p -> p.getTitle().contains(title)).collect(Collectors.toList());
        }
        if (name!=null){
            players = players.stream().filter(p -> p.getName().contains(name)).collect(Collectors.toList());
        }
        if (race!=null){
            players = players.stream().filter(p -> p.getRace().equals(race)).collect(Collectors.toList());
        }
        if (profession!=null){
            players = players.stream().filter(p -> p.getProfession().equals(profession)).collect(Collectors.toList());
        }
        if (after!=null){
            players = players.stream().filter(p -> p.getBirthday().getTime()>=after).collect(Collectors.toList());
        }
        if (before!=null){
            players = players.stream().filter(p -> p.getBirthday().getTime()<=before).collect(Collectors.toList());
        }
        if (banned!=null){
            players = players.stream().filter(p -> p.getBanned().equals(banned)).collect(Collectors.toList());
        }
        if (minExperience!=null){
            players = players.stream().filter(p -> p.getExperience()>=minExperience).collect(Collectors.toList());
        }
        if (maxExperience!=null){
            players = players.stream().filter(p -> p.getExperience()<=maxExperience).collect(Collectors.toList());
        }
        if (minLevel!=null){
            players = players.stream().filter(p -> p.getLevel()>=minLevel).collect(Collectors.toList());
        }
        if (maxLevel!=null){
            players = players.stream().filter(p -> p.getLevel()<=maxLevel).collect(Collectors.toList());
        }

        return players;
    }

    @Override
    public Integer getPlayersCount(String name, String title, Race race, Profession profession, Long after,
                                   Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                   Integer minLevel, Integer maxLevel
    ) {
        List<Player> players = getFilteredAndSortedPlayersList(name,title,race,profession,after,before,banned,minExperience,maxExperience,minLevel,maxLevel,PlayerOrder.ID);
        return players.size();
    }

    @Override
    public List<Player> getPagedPlayersList(List<Player> players, int pageNumber, int pageSize) {
        List<Player> pagedPlayersList = new ArrayList<>();
        int skip = pageNumber * pageSize;
        int last = skip + pageSize;
        for (int i = 0; i<players.size(); i++){
            if (i >= skip && i < last){
                pagedPlayersList.add(players.get(i));
            }
        }

        return pagedPlayersList;
    }

    @Override
    public Player getPlayer(Long id) {
        System.out.println(playerRepository.getOne(id));
        return playerRepository.getOne(id);
    }

    @Override
    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }

    @Override
    public Player createPlayer(String name,String title,Race race,Profession profession,Date birthday, Boolean banned, Integer experience) {
        Player player = new Player();
        player.setName(name);
        player.setTitle(title);
        player.setRace(race);
        player.setProfession(profession);
        player.setBirthday(birthday);
        player.setBanned(banned);
        player.setExperience(experience);
        player.setLevelAndUntilNextLevel();
        return player;
    }

    @Override
    public ResponseEntity<Player> updatePlayer(Long id, String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience) {



        Player player = playerRepository.getOne(id);
        HttpStatus httpStatus = HttpStatus.OK;
        if (name!=null) {
            if (!name.equals("") && name.length()<=12) {
                player.setName(name);
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }
        if (title!=null) {
            if (title.length()<=30) {
                player.setTitle(title);
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }
        if (race!=null) {
            player.setRace(race);
        }
        if (profession!=null) {
            player.setProfession(profession);
        }
        if (birthday!=null) {
            if (birthday.getTime()>=946587600000L && birthday.getTime()<=32503582800000L) { //32693500800000
                player.setBirthday(birthday);
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }
        if (banned!=null) {
            player.setBanned(banned);
        }
        if (experience!=null) {
            if (experience >=0 && experience <= 10000000) {
                player.setExperience(experience);
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }
        player.setLevelAndUntilNextLevel();
        return new ResponseEntity<>(player,httpStatus);
    }

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }
}
