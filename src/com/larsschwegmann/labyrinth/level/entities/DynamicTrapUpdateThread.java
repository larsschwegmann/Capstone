package com.larsschwegmann.labyrinth.level.entities;

import java.util.ArrayList;
import java.util.TimerTask;

public class DynamicTrapUpdateThread extends TimerTask {
    
    private ArrayList<DynamicTrap> trapsToUpdate;
    
    public DynamicTrapUpdateThread(ArrayList<DynamicTrap> trapsToUpdate) {
        this.trapsToUpdate = trapsToUpdate;
    }
    
    @Override
    public void run() {
        for (DynamicTrap d : trapsToUpdate) {
            d.update();
        }
    }
    
}
