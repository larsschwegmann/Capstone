package com.larsschwegmann.labyrinth.level.entities;

import com.googlecode.lanterna.terminal.Terminal;

public abstract class Entity {

    public enum Direction {
        Up, Right, Down, Left
    }

    protected int x;
    protected int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Entity() {
        this.x = 0;
        this.y = 0;
    }

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract int getRawValue();

    public abstract boolean isStatic();

    public void render(Terminal t, int x, int y) {
        t.moveCursor(x, y);
        t.applyForegroundColor(getForegroundColor());
        t.applyBackgroundColor(getBackgroundColor());
        t.putCharacter(getSymbol());
    }

    public abstract Terminal.Color getForegroundColor();
    public abstract Terminal.Color getBackgroundColor();
    public abstract char getSymbol();

    public abstract void update();
}
