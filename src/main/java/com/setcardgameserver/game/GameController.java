package com.setcardgameserver.game;

import com.setcardgameserver.config.ConnectRequest;
import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.exception.InvalidParamException;
import com.setcardgameserver.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/start")
    public ResponseEntity<Game> start(@RequestBody UUID player) {
        log.info("start game request: {}", player);
        return ResponseEntity.ok(gameService.createGame(player));
    }

    @MessageMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(gameService.connectToGame(request.getPlayerId(), request.getGameId()));
    }

    @MessageMapping("/connect/random")
    public Game connectRandom(@RequestBody UUID player) throws NotFoundException {
        log.info("connect random {}", player);

        JSONObject jsonPlayer = new JSONObject();
        jsonPlayer.put("player", player.toString());

        Game game = gameService.connectToRandomGame(player);
        simpMessagingTemplate.convertAndSend("/topic/alma", game);

        return game;
    }

    @MessageMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGameException {
        log.info("gameplay: {}", request);
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }

    @MessageMapping("/gameplay/button")
    public ResponseEntity<Game> buttonPress(@RequestBody GamePlayButtonPress buttonPress) throws NotFoundException, InvalidGameException {
        log.info("buttonPress: {}", buttonPress);
        Game game = gameService.buttonPress(buttonPress);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }

    @MessageMapping("/alma")
    public String alma(@RequestBody String player){
        return "alma";
    }

    //kell delete game
}
