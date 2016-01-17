package com.larsschwegmann.labyrinth.level.entities;

import java.util.ArrayList;
import java.util.TimerTask;

public class DynamicTrapUpdateThread extends TimerTask {
    
    private ArrayList<DynamicTrap> trapsToUpdate;
    //private boolean isPaused = true;
    
    public DynamicTrapUpdateThread(ArrayList<DynamicTrap> trapsToUpdate) {
        this.trapsToUpdate = trapsToUpdate;
    }
    /*
    public void stopUpdating() {
        isPaused = true;
    }
    */
    /*
    public void startUpdating() {
        isPaused = false;
        if (!this.isAlive()) {
            this.start();
        }
    }
    */
    @Override
    public void run() {
        /*
        while (!isPaused) {
            for (DynamicTrap d : trapsToUpdate) {
                d.update();
            }
        }
        */
        for (DynamicTrap d : trapsToUpdate) {
            d.update();
        }
    }
    
}
