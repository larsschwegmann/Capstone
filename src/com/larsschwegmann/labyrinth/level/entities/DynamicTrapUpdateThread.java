package com.larsschwegmann.labyrinth.level.entities;

import java.util.ArrayList;

public class DynamicTrapUpdateThread extends Thread {
    
    private ArrayList<DynamicTrap> trapsToUpdate;
    private boolean isPaused = true;
    
    public DynamicTrapUpdateThread(ArrayList<DynamicTrap> trapsToUpdate) {
        this.trapsToUpdate = trapsToUpdate;
    }
    
    public void stopUpdating() {
        isPaused = true;
    }
    
    public void startUpdating() {
        isPaused = false;
        if (!this.isAlive()) {
            this.start();
        }
    }
    
    @Override
    public void run() {
        while (!isPaused) {
            for (DynamicTrap d : trapsToUpdate) {
                d.update();
            }
        }
    }
    
}
