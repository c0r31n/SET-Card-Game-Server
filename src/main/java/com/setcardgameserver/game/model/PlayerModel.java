package com.setcardgameserver.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class PlayerModel {
    private UUID username;

    public PlayerModel() {
    }

    public PlayerModel(String username) {
        this.username = UUID.fromString(username);
    }
}
