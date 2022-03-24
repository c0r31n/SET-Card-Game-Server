package com.setcardgameserver.game.model;

import lombok.Data;

@Data
public class PlayerModel {
    private String username;

    public PlayerModel() {
    }

    public PlayerModel(String username) {
        this.username = username;
    }
}
