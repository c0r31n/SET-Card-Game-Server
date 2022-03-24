package com.setcardgameserver.game.model;

import lombok.Data;

@Data
public class DestroyGameModel {
    private int gameId;

    public DestroyGameModel() {
    }

    public DestroyGameModel(int gameId) {
        this.gameId = gameId;
    }
}
