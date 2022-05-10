package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List<Player> getPlayersList(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                       @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                                       @RequestParam(value = "order",defaultValue = "ID") PlayerOrder playerOrder,
                                       // далее для фильтров
                                       @RequestParam(value = "name",required = false) String name,
                                       @RequestParam(value = "title",required = false) String title,
                                       @RequestParam(value = "race",required = false) Race race,
                                       @RequestParam(value = "profession",required = false) Profession profession,
                                       @RequestParam(value = "after",required = false) Long after,
                                       @RequestParam(value = "before",required = false) Long before,
                                       @RequestParam(value = "banned",required = false) Boolean banned,
                                       @RequestParam(value = "minExperience",required = false) Integer minExperience,
                                       @RequestParam(value = "maxExperience",required = false) Integer maxExperience,
                                       @RequestParam(value = "minLevel",required = false) Integer minLevel,
                                       @RequestParam(value = "maxLevel",required = false) Integer maxLevel

    ) {
        //System.out.println("getPlayersList");
        //System.out.println("pageNumber= "+pageNumber+", pageSize= " + pageSize + ", order= " + playerOrder);

        List<Player> players =  playerService.getFilteredAndSortedPlayersList(name,title,race,profession,after,before,banned,
                minExperience,maxExperience,minLevel,maxLevel,playerOrder);

        players = playerService.getPagedPlayersList(players,pageNumber,pageSize);

        return players;
    }

    @GetMapping("/players/count")
    public Integer getPlayersCount(@RequestParam(value = "name",required = false) String name,
                                   @RequestParam(value = "title",required = false) String title,
                                   @RequestParam(value = "race",required = false) Race race,
                                   @RequestParam(value = "profession",required = false) Profession profession,
                                   @RequestParam(value = "after",required = false) Long after,
                                   @RequestParam(value = "before",required = false) Long before,
                                   @RequestParam(value = "banned",required = false) Boolean banned,
                                   @RequestParam(value = "minExperience",required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience",required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel",required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel",required = false) Integer maxLevel
    ) {
        //System.out.println("getPlayersCount");
        return playerService.getPlayersCount(name,title,race,profession,after,before,banned,minExperience,maxExperience,minLevel,maxLevel);
    }

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {

        //System.out.println("createPlayer");
        //System.out.println(player);
        // null checks
        if (player.getName()==null || player.getTitle()==null
                || player.getRace()==null || player.getProfession() == null
                || player.getBirthday() == null || player.getExperience()==null)
        {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

        if (player.getName().equals("") || player.getName().length()>12 || player.getTitle().length()>30
                || player.getBirthday().getTime()<946587600000L || player.getBirthday().getTime()>32503582800000L
                || player.getExperience()<0 || player.getExperience() > 10000000)
        {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

        Player newPlayer = playerService.createPlayer(player.getName(), player.getTitle(), player.getRace(), player.getProfession(), player.getBirthday(),
                player.getBanned()==null? false: player.getBanned(), player.getExperience());
        return new ResponseEntity<>(Hibernate.unproxy(playerService.savePlayer(newPlayer),Player.class),HttpStatus.OK);




    }

    @GetMapping("players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        //System.out.println("getPlayerById" + " id= " + id);
        if (id<=0) return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        Player player = Hibernate.unproxy(playerService.getPlayer(id),Player.class);
        return new ResponseEntity<>(player,HttpStatus.OK);
    }

    @PostMapping("players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        //System.out.println("updatePlayer");
        ResponseEntity<Player> responseEntity = new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);

        if (id<=0){
            return responseEntity;
        }


        responseEntity = playerService.updatePlayer(id, player.getName(), player.getTitle(), player.getRace(),
                player.getProfession(), player.getBirthday(), player.getBanned(), player.getExperience());

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            player = responseEntity.getBody();
            return new ResponseEntity<>(Hibernate.unproxy(playerService.savePlayer(player), Player.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable Long id) {
        //System.out.println("deletePlayer");
        if (id>0){
            Player player = playerService.getPlayer(id);
            playerService.deletePlayer(player);
            return new ResponseEntity<>(null,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }



}
