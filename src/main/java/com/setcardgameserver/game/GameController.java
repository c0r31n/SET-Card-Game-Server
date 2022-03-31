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
@AllArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/create")
    public Game start(@RequestBody PlayerModel player) {
        System.out.println("create private game request: " + player.getUsername());

        Game game = null;
        try {
            game = gameService.createGame(UUID.fromString(player.getUsername()));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect")
    public Game connect(@RequestBody ConnectRequest request){
        System.out.println("connect to private game request: " + request.getGameId()+ " " + request.getPlayerId());

        Game game = gameService.connectToGame(request.getPlayerId(), request.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);
        return game;
    }

    @MessageMapping("/connect/random")
    public Game connectRandom(@RequestBody PlayerModel player) throws NotFoundException {
        System.out.println("connect random " + player.getUsername());

        JSONObject jsonPlayer = new JSONObject();
        jsonPlayer.put("player", player.getUsername());

        Game game = gameService.connectToRandomGame(UUID.fromString(player.getUsername()));
        simpMessagingTemplate.convertAndSend("/topic/waiting", game);

        return game;
    }

    @MessageMapping("/gameplay")
    public Game gamePlay(@RequestBody GameplayModel gameplay) throws NotFoundException, InvalidGameException {
        System.out.println("gameplay: " + gameplay.getGameId()+ " " + gameplay.getPlayerId());

        Game game = gameService.gameplay(gameplay);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/gameplay/button")
    public Game buttonPress(@RequestBody GameplayButtonPressModel buttonPress) throws NotFoundException, InvalidGameException {
        System.out.println("buttonPress: "+ buttonPress.getGameId() + " " + buttonPress.getPlayerId());

        Game game = gameService.buttonPress(buttonPress);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return game;
    }

    @MessageMapping("/game/destroy")
    public String destroyGame(@RequestBody GameIdModel gameId) throws NotFoundException {
        System.out.println("destroy game " + gameId.getGameId());

        gameService.removeGame(gameId.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/destroyed/" + gameId.getGameId(), "Done");
        return "Done";
    }

    @MessageMapping("/all/destroy")
    public void destroyAllGames(@RequestBody PlayerModel player){
        System.out.println("destroy all games by " + player.getUsername());

        gameService.destroyAllGames();
    }

    @MessageMapping("/start")
    public Game startGame(@RequestBody GameIdModel gameId) throws NotFoundException {
        System.out.println("game started: " + gameId.getGameId());

        Game game = gameService.getGameById(gameId.getGameId());
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameId.getGameId(), game);

        return game;
    }
}
