package com.setcardgameserver.card;

public enum Quantity {
    ONE("1"),
    TWO("2"),
    THREE("3");

    public final String label;

    Quantity(String label) {
        this.label = label;
    }
}
