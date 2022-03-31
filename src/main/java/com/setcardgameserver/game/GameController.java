package com.setcardgameserver.game;

import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.exception.NotFoundException;
import com.setcardgameserver.game.model.*;
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
    public SimplifiedGameModel start(@RequestBody PlayerModel player) {
        System.out.println("create private game request: " + player.getUsername());


        SimplifiedGameModel game = null;
        try {
            game = new SimplifiedGameModel(gameService.createGame(UUID.fromString(player.getUsername())));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect")
    public SimplifiedGameModel connect(@RequestBody ConnectRequest request) {
        System.out.println("connect to private game request: " + request.getGameId() + " " + request.getPlayerId());

        SimplifiedGameModel game = new SimplifiedGameModel(gameService.connectToGame(request.getPlayerId(), request.getGameId()));
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect/random")
    public SimplifiedGameModel connectRandom(@RequestBody PlayerModel player) {
        System.out.println("connect random " + player.getUsername());

        JSONObject jsonPlayer = new JSONObject();
        jsonPlayer.put("player", player.getUsername());

        SimplifiedGameModel game = null;
        try {
            game = new SimplifiedGameModel(gameService.connectToRandomGame(UUID.fromString(player.getUsername())));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);

        return game;
    }

    @MessageMapping("/gameplay")
    public SimplifiedGameModel gamePlay(@RequestBody GameplayModel gameplay) {
        System.out.println("gameplay: " + gameplay.getGameId() + " " + gameplay.getPlayerId());

        SimplifiedGameModel game = null;
        try {
            game = new SimplifiedGameModel(gameService.gameplay(gameplay));
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (InvalidGameException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/gameplay/button")
    public SimplifiedGameModel buttonPress(@RequestBody GameplayButtonPressModel buttonPress) {
        System.out.println("buttonPress: " + buttonPress.getGameId() + " " + buttonPress.getPlayerId());

        SimplifiedGameModel game = null;
        try {
            game = new SimplifiedGameModel(gameService.buttonPress(buttonPress));
        } catch (InvalidGameException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/game/destroy")
    public String destroyGame(@RequestBody GameIdModel gameId) {
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
    public void destroyAllGames(@RequestBody PlayerModel player) {
        System.out.println("destroy all games by " + player.getUsername());

        gameService.destroyAllGames();
    }

    @MessageMapping("/start")
    public SimplifiedGameModel startGame(@RequestBody GameIdModel gameId) {
        System.out.println("game started: " + gameId.getGameId());

        SimplifiedGameModel game = null;
        try {
            game = new SimplifiedGameModel(gameService.getGameById(gameId.getGameId()));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameId.getGameId(), game);

        return game;
    }
}
