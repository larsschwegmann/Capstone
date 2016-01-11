package com.larsschwegmann.labyrinth;

public class Main {

    /**
     * Main
     * @param args Program arguments
     */
    public static void main(String[] args) {
        GameStateManager g = GameStateManager.sharedInstance();
        g.boot();
    }

}
