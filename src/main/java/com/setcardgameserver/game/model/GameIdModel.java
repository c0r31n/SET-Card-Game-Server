package com.setcardgameserver.game.model;

import lombok.Data;

@Data
public class GameIdModel {
    private int gameId;

    public GameIdModel() {
    }

    public GameIdModel(int gameId) {
        this.gameId = gameId;
    }
}
