package com.larsschwegmann.labyrinth.level.entities;

public class EntityFactory {

    public static Entity createEntity(int rawValue, int x, int y) {
        Entity et = null;
        switch (rawValue) {
            case 0:
                //Wall
                et = new Wall(x, y);
                break;
            case 1:
                //Entrance
                et = new Entrance(x, y);
                break;
            case 2:
                //Exit
                et = new Exit(x, y);
                break;
            case 3:
                //Static Trap
                et = new StaticTrap(x, y);
                break;
            case 4:
                //Dynamic Trap
                et = new DynamicTrap(x, y);
                break;
            case 5:
                //Key
                et = new Key(x, y);
                break;
            default:
                //Error
                et = null;
                break;
        }
        return et;
    }

    public static Player createPlayer(int x, int y) {
        return new Player(x, y);
    }

}
