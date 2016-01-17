package com.larsschwegmann.labyrinth.level.entities;

import com.googlecode.lanterna.terminal.Terminal;
import com.larsschwegmann.labyrinth.scenes.Game;
import com.larsschwegmann.labyrinth.GameStateManager;

import java.util.ArrayList;

public class Player extends Entity {

    private int livesLeft;

    private ArrayList inventory;

    private int prevX;
    private int prevY;

    public int getLivesLeft() {
        return livesLeft;
    }

    public void setLivesLeft(int livesLeft) {
        this.livesLeft = livesLeft;
    }

    public int getPrevX() {
        return prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public ArrayList getInventory() {
        return inventory;
    }

    public void addToInventory(Object o) {
        this.inventory.add(o);
    }

    public void removeFromInventory(Object o) {
        this.inventory.remove(o);
    }

    public boolean inventoryContainsObject(Object o) {
        return this.inventory.contains(o);
    }

    public boolean inventoryContainsObjectOfClass(Class c) {
        boolean contained = false;
        for (Object o : this.inventory) {
            if (o.getClass() == c) {
                contained = true;
            }
        }
        return contained;
    }

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.livesLeft = 100;
        this.inventory = new ArrayList();
    }

    @Override
    public Terminal.Color getForegroundColor() {
        return Terminal.Color.GREEN;
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
        return '\u263B';
    }

    public boolean move (Direction d) {
        //Calculate new Position
        int newX = this.x;
        int newY = this.y;
        switch (d) {
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
        }
        Entity objectAtNewPos = GameStateManager.sharedInstance().getGame().getLevel().getEntityAtIndex(newX, newY);
        if ((objectAtNewPos == null || !(objectAtNewPos instanceof Wall)) && newX >= 0 && newY >= 0 && newX < GameStateManager.sharedInstance().getGame().getLevel().getWidth() && newY < GameStateManager.sharedInstance().getGame().getLevel().getHeight()) {
            this.prevX = this.x;
            this.prevY = this.y;
            this.x = newX;
            this.y = newY;
            //System.out.println("p: x:" + this.x + " y:" + this.y);
            return true;
        } else {
            //Can't walk through walls
            return false;
        }
    }

    @Override
    public void update() {
        //DO Something
    }

    @Override
    public int getRawValue() {
        return 6;
    }
}
