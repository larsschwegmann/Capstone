package com.larsschwegmann.labyrinth.level.entities;

import com.googlecode.lanterna.terminal.Terminal;

public class StaticTrap extends Entity {

    public StaticTrap(int x, int y) {
        super(x, y);
    }

    @Override
    public Terminal.Color getForegroundColor() {
        return Terminal.Color.RED;
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
        return 'F';
    }

    @Override
    public void update() {
        //DO Something
    }

    @Override
    public int getRawValue() {
        return 3;
    }
}
