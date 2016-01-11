package com.larsschwegmann.labyrinth.level.entities;

import com.googlecode.lanterna.terminal.Terminal;
import com.larsschwegmann.labyrinth.Game;
import com.larsschwegmann.labyrinth.GameStateManager;
import com.larsschwegmann.labyrinth.level.Level;

import java.util.Random;

public class DynamicTrap extends Entity {

    private long lastMoved;

    private int prevX;
    private int prevY;

    public int getPrevX() {
        return prevX;
    }

    public void setPrevX(int prevX) {
        this.prevX = prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public void setPrevY(int prevY) {
        this.prevY = prevY;
    }

    public DynamicTrap(int x, int y) {
        super(x, y);
        this.prevX = this.x;
        this.prevY = this.y;
        lastMoved = System.currentTimeMillis();
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
        return false;
    }

    @Override
    public char getSymbol() {
        return 'D';
    }

    @Override
    public void update() {
        //DO Something
        //MOVE BRUH

        if (System.currentTimeMillis() - lastMoved > 500) {
            this.lastMoved = System.currentTimeMillis();

            Player p = GameStateManager.sharedInstance().getGame().getLevel().getPlayer();
            if (p.getX() != this.x || p.getY() != this.y) {
                //If Player is on the same field, don't move
                boolean foundPossibleDirection = false;
                while (!foundPossibleDirection) {
                    Direction randomDir = randomDirection();
                    int newX = this.x;
                    int newY = this.y;

                    switch (randomDir) {
                        case Up:
                            newY--;
                            break;
                        case Right:
                            newX++;
                            break;
                        case Down:
                            newY++;
                            break;
                        case Left:
                            newX--;
                            break;
                        default:
                            break;
                    }

                    Level level = GameStateManager.sharedInstance().getGame().getLevel();
                    Entity oldEntity = level.getEntityAtIndex(newX, newY);
                    if (oldEntity == null) {
                        level.setEntityAtIndex(null, this.x, this.y);
                        this.prevX = this.x;
                        this.prevY = this.y;
                        this.x = newX;
                        this.y = newY;
                        level.setEntityAtIndex(this, this.x, this.y);
                        foundPossibleDirection = true;
                    }
                }
            }
        }
    }

    private Direction randomDirection() {
        int dir = new Random().nextInt(Direction.values().length);
        return Direction.values()[dir];
    }

    @Override
    public int getRawValue() {
        return 4;
    }
}
