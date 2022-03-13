package com.setcardgameserver.game;

import lombok.Data;

import java.util.UUID;

@Data
public class GamePlayButtonPress {

    private int gameId;
    private UUID playerId;
}
