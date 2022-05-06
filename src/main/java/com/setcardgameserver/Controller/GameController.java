package com.setcardgameserver.Controller;

import com.setcardgameserver.DTO.*;
import com.setcardgameserver.Exception.InvalidGameException;
import com.setcardgameserver.Exception.NotFoundException;
import com.setcardgameserver.Service.GameService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/create")
    public Game start(@RequestBody Player player) {
        System.out.println("create private game request: " + player.getUsername());


        Game game = null;
        try {
            game = new Game(gameService.createGame(UUID.fromString(player.getUsername())));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect")
    public Game connect(@RequestBody ConnectRequest request) {
        System.out.println("connect to private game request: " + request.getGameId() + " " + request.getPlayerId());

        Game game = new Game(gameService.connectToGame(request.getPlayerId(), request.getGameId()));
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect/random")
    public Game connectRandom(@RequestBody Player player) {
        System.out.println("connect random " + player.getUsername());

        JSONObject jsonPlayer = new JSONObject();
        jsonPlayer.put("player", player.getUsername());

        Game game = null;
        try {
            game = new Game(gameService.connectToRandomGame(UUID.fromString(player.getUsername())));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);

        return game;
    }

    @MessageMapping("/gameplay")
    public Game gamePlay(@RequestBody Gameplay gameplay) {
        System.out.println("gameplay: " + gameplay.getGameId() + " " + gameplay.getPlayerId());

        Game game = null;
        try {
            game = new Game(gameService.gameplay(gameplay));
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (InvalidGameException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/gameplay/button")
    public Game buttonPress(@RequestBody GameplayButtonPress buttonPress) {
        System.out.println("buttonPress: " + buttonPress.getGameId() + " " + buttonPress.getPlayerId());

        Game game = null;
        try {
            game = new Game(gameService.buttonPress(buttonPress));
        } catch (InvalidGameException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/game/destroy")
    public String destroyGame(@RequestBody GameId gameId) {
        System.out.println("destroy game " + gameId.getGameId());

        try {
            gameService.removeGame(gameId.getGameId());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/destroyed/" + gameId.getGameId(), "Done");
        return "Done";
    }

    @MessageMapping("/all/destroy")
    public void destroyAllGames(@RequestBody Player player) {
        System.out.println("destroy all games by " + player.getUsername());

        gameService.destroyAllGames();
    }

    @MessageMapping("/start")
    public Game startGame(@RequestBody GameId gameId) {
        System.out.println("game started: " + gameId.getGameId());

        Game game = null;
        try {
            game = new Game(gameService.getGameById(gameId.getGameId()));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameId.getGameId(), game);

        return game;
    }
}
