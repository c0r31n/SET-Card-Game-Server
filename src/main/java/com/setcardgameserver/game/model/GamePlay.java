package com.setcardgameserver.game.model;

import lombok.Data;

import java.util.UUID;

@Data
public class GamePlay {

    private int gameId;
    private UUID playerId;
    private boolean select;  //selected = true | deselected = false
    private int selectedCardIndex;
}
