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

    @MessageMapping("/create")
    public Game start(@RequestBody PlayerModel player) {
//        log.info("create private game request: {}", player.getUsername());
        System.out.println("\n\ncreate private game request: " + player.getUsername()+"\n\n");

        Game game = gameService.createGame(UUID.fromString(player.getUsername()));
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect")
    public Game connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
//        log.info("connect to private game request: {}", request.getPlayerId());
//        System.out.println("\n\nconnect to private game request: " + request.getGameId()+ " " + request.getPlayerId() +"\n\n");
        System.out.println("\n\nconnect to private game request: a" + request.getGameId()+"a\n\n");

        Game game = gameService.connectToGame(request.getPlayerId(), request.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect/random")
    public Game connectRandom(@RequestBody PlayerModel player) throws NotFoundException {
//        log.info("connect random {}", player.getUsername().toString());
        System.out.println("\n\nconnect random " + player.getUsername().toString()+"\n\n");

        JSONObject jsonPlayer = new JSONObject();
        jsonPlayer.put("player", player.getUsername());

        Game game = gameService.connectToRandomGame(UUID.fromString(player.getUsername()));
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);

        return game;
    }

    @MessageMapping("/gameplay")
    public Game gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGameException {
//        log.info("gameplay: {}", request.getPlayerId());
        System.out.println("\n\ngameplay: " + request.getGameId()+ " " + request.getPlayerId() + "\n\n");

        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/gameplay/button")
    public Game buttonPress(@RequestBody GamePlayButtonPress buttonPress) throws NotFoundException, InvalidGameException {
//        log.info("buttonPress: {}", buttonPress.getPlayerId());
        System.out.println("\n\nbuttonPress:  " + buttonPress.getPlayerId()+"\n\n");

        Game game = gameService.buttonPress(buttonPress);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/game/destroy")
    public String destroyGame(@RequestBody GameIdModel gameId) throws NotFoundException {
//        log.info("destroy game {}", gameId.getGameId());
        System.out.println("\n\ndestroy game " + gameId.getGameId()+"\n\n");

        gameService.removeGame(gameId.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/destroyed/" + gameId.getGameId(), "Done");
        return "Done";
    }

    @MessageMapping("/all/destroy")
    public void destroyAllGames(@RequestBody PlayerModel player){
//        log.info("destroy all games by {}", player.getUsername());
        System.out.println("\n\ndestroy all games by " + player.getUsername() +"\n\n");

        gameService.destroyAllGames();
    }

    @MessageMapping("/start")
    public Game startGame(@RequestBody GameIdModel gameId) throws NotFoundException {
//        log.info("game started: {}", gameId.getGameId());
        System.out.println("\n\ngame started: " + gameId.getGameId()+"\n\n");

        Game game = gameService.getGameById(gameId.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameId.getGameId(), game);

        return game;
    }
}
