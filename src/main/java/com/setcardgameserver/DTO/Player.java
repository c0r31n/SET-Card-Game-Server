package com.setcardgameserver.DTO;

import lombok.Data;

@Data
public class Player {
    private String username;

    public Player() {
    }

    public Player(String username) {
        this.username = username;
    }
}
