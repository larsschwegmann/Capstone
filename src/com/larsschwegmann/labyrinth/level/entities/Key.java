package com.larsschwegmann.labyrinth.level.entities;

import com.googlecode.lanterna.terminal.Terminal;

public class Key extends Entity {

    public Key(int x, int y) {
        super(x, y);
    }

    @Override
    public Terminal.Color getForegroundColor() {
        return Terminal.Color.YELLOW;
    }

    @Override
    public Terminal.Color getBackgroundColor() {
        return Terminal.Color.BLACK;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public char getSymbol() {
        return 'K';
    }

    @Override
    public void update() {
        //DO Something
    }

    @Override
    public String toString() {
        return "Schlüssel";
    }

    @Override
    public int getRawValue() {
        return 5;
    }
}
