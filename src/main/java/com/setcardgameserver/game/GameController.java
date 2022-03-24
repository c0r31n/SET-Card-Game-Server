package com.setcardgameserver.game;

import com.setcardgameserver.game.model.ConnectRequest;
import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.exception.InvalidParamException;
import com.setcardgameserver.exception.NotFoundException;
import com.setcardgameserver.game.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/start")
    public Game start(@RequestBody UUID player) {
        log.info("start game request: {}", player);
        return gameService.createGame(player);
    }

    @MessageMapping("/connect")
    public Game connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        return gameService.connectToGame(request.getPlayerId(), request.getGameId());
    }

    @MessageMapping("/connect/random")
    public Game connectRandom(@RequestBody PlayerModel player) throws NotFoundException {
        log.info("connect random {}", player.getUsername().toString());

        JSONObject jsonPlayer = new JSONObject();
        jsonPlayer.put("player", player.getUsername());

        Game game = gameService.connectToRandomGame(UUID.fromString(player.getUsername()));
        simpMessagingTemplate.convertAndSend("/topic/alma", game);

        return game;
    }

    @MessageMapping("/gameplay")
    public Game gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGameException {
        log.info("gameplay: {}", request);
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/gameplay/button")
    public Game buttonPress(@RequestBody GamePlayButtonPress buttonPress) throws NotFoundException, InvalidGameException {
        log.info("buttonPress: {}", buttonPress);
        Game game = gameService.buttonPress(buttonPress);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/game/destroy")
    public void destroyGame(@RequestBody DestroyGameModel gameId) throws NotFoundException {
        log.info("destroy game {}", gameId.getGameId());

        gameService.removeGame(gameId.getGameId());
    }

    @MessageMapping("/all/destroy")
    public void destroyAllGames(@RequestBody PlayerModel player){
        log.info("destroy all games by {}", player.getUsername());
        gameService.destroyAllGames();
    }

    @MessageMapping("/ping")
    public String ping(PingPong ping){
        return "pong";
    }
}
