package com.setcardgameserver.DTO;

import lombok.Data;

@Data
public class GameId {
    private int gameId;

    public GameId() {
    }

    public GameId(int gameId) {
        this.gameId = gameId;
    }
}
