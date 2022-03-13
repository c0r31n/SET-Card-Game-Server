package com.setcardgameserver.card;

public enum Difficulty {
    EASY("easy"),
    NORMAL("normal");

    public final String label;

    Difficulty(String label) {
        this.label = label;
    }
}
