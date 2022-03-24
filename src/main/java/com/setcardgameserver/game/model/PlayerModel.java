package com.setcardgameserver.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class PlayerModel {
    private String username;

    public PlayerModel() {
    }

    public PlayerModel(String username) {
        this.username = username;
    }
}
