package com.larsschwegmann.labyrinth.level.entities;

import com.googlecode.lanterna.terminal.Terminal;

public class Wall extends Entity {

    public Wall(int x, int y) {
        super(x, y);
    }

    @Override
    public Terminal.Color getForegroundColor() {
        return Terminal.Color.WHITE;
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
        return '\u2593';
    }

    @Override
    public void update() {
        //Do Something
    }

    @Override
    public int getRawValue() {
        return 0;
    }
}
