package com.setcardgameserver.game;

import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.exception.InvalidParamException;
import com.setcardgameserver.exception.NotFoundException;
import com.setcardgameserver.game.model.Game;
import com.setcardgameserver.game.model.GamePlay;
import com.setcardgameserver.game.model.GamePlayButtonPress;
import com.setcardgameserver.game.model.GameStatus;
import com.setcardgameserver.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    public Game createGame(UUID player) throws NotFoundException{
        Optional<Game> hasGame = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getPlayer1().equals(player))
                .findFirst();

        if (hasGame.isPresent()){
            Game game = GameStorage.getInstance().getGames().values().stream()
                    .filter(it -> it.getPlayer1().equals(player))
                    .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
            removeGame(game.getGameId());
        }

        Game game = new Game();
        game.createGame();
        int newGameId;

        do {
            newGameId = new Random().nextInt(99999);
        }while(GameStorage.getInstance().getGames().containsKey(newGameId));

        game.setGameId(newGameId);
        game.setPlayer1(player);
        game.getPoints().put(player,0);
        game.setStatus(GameStatus.WAITING);
        GameStorage.getInstance().setGame(game);

        return game;
    }

    public Game connectToGame(UUID player2, int gameId){
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            return new Game();
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);

        if (game.getPlayer2() != null) {
            return new Game();
        }

        game.setPlayer2(player2);
        game.getPoints().put(player2,0);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToRandomGame(UUID player2) throws NotFoundException {
        Game game;
        Optional<Game> hasGame = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(GameStatus.NEW))
                .findFirst();

        if (hasGame.isEmpty()){
            game = createNewRandomGame(player2);
            System.out.println("isEmpty");
            return game;
        }
        else if (hasGame.isPresent()){
            game = GameStorage.getInstance().getGames().values().stream()
                    .filter(it -> it.getStatus().equals(GameStatus.NEW))
                    .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));

            if (game.getPlayer1().toString().equals(player2.toString())){
                removeGame(game.getGameId());
                game = createNewRandomGame(player2);
                System.out.println("same game");
                return game;
            }

            if (game.getPlayer2() != null){
                if (game.getPlayer2().toString().equals(player2.toString())){
                    GameStorage.getInstance().removeGame(game);
                    game = createNewRandomGame(player2);
                    System.out.println("left game");
                    return game;
                }
            }

            game.setPlayer2(player2);
            game.getPoints().put(player2,0);
            game.setStatus(GameStatus.IN_PROGRESS);
            GameStorage.getInstance().setGame(game);
            System.out.println("isPresent");
            return game;
        }

        System.out.println("nothing");
        return null;
    }

    public Game createNewRandomGame(UUID player){
        Game newGame = new Game();
        newGame.createGame();
        newGame.setGameId(new Random().nextInt(99999));
        newGame.setPlayer1(player);
        newGame.getPoints().put(player,0);
        newGame.setStatus(GameStatus.NEW);
        GameStorage.getInstance().setGame(newGame);

        return newGame;
    }

    public Game buttonPress(GamePlayButtonPress buttonPress) throws InvalidGameException, NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(buttonPress.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(buttonPress.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            GameStorage.getInstance().removeGame(game);
            throw new InvalidGameException("Game is already finished");
        }

        if (game.getBlockedBy()==buttonPress.getPlayerId()){
            game.setBlockedBy(null);
        }

        game.setBlockedBy(buttonPress.getPlayerId());

        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            GameStorage.getInstance().removeGame(game);
            throw new InvalidGameException("Game is already finished");
        }

        if (game.getBlockedBy()!=null){
            if (gamePlay.isSelect()){
                game.addToSelectedCardIndexes(gamePlay.getSelectedCardIndex());

                if (game.getSelectedCardIndexSize()==3){
                    if(game.hasSet(game.getCardsFromIndex(game.getSelectedCardIndexes()))){
                        game.getPoints().put(gamePlay.getPlayerId(),game.getPoints().get(gamePlay.getPlayerId())+1);
                        game.changeCardsOnBoard();
                        if (!game.hasSet(game.getBoard())){
                            game.setWinner(gamePlay.getPlayerId());
                            game.setStatus(GameStatus.FINISHED);
                            GameStorage.getInstance().removeGame(game);
                        }
                    }
                    else{
                        game.clearSelectedCardIndexes();
                    }
                    game.setBlockedBy(null);
                }
            }
            else {
                game.removeFromSelectedCardIndexes(gamePlay.getSelectedCardIndex());
            }
        }

        return game;
    }

    public Game getGameById(int gameId) throws NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new NotFoundException("Game not found");
        }
        return GameStorage.getInstance().getGames().get(gameId);
    }

    public void removeGame(int gameId) throws NotFoundException{
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);
        GameStorage.getInstance().removeGame(game);
        System.out.println("\nGame removed\n");
    }

    public void destroyAllGames(){
        GameStorage.getInstance().removeAllGames();
    }
}
