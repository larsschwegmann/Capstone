package com.larsschwegmann.labyrinth.scenes;

import com.larsschwegmann.labyrinth.AudioManager;
import com.larsschwegmann.labyrinth.GameStateManager;

public class PauseMenu extends MainMenu{

    public PauseMenu() {
        this.menuElements = new String[]{"Weiterspielen", "Legende ansehen", "Spiel laden", "Spiel speichern und beenden", "Zurück zum Hauptmenü"};
        this.titleArt =
                "   ___                     \n" +
                "  / _ \\__ _ _   _ ___  ___ \n" +
                " / /_)/ _` | | | / __|/ _ \\\n" +
                "/ ___/ (_| | |_| \\__ \\  __/\n" +
                "\\/    \\__,_|\\__,_|___/\\___|\n" +
                "                           ";
    }

    @Override
    public void update() {
        super.update();
        com.googlecode.lanterna.input.Key input = GameStateManager.sharedInstance().readInput();
        if (input != null) {
            switch (input.getKind()) {
                case Escape:
                    //Return to game
                    AudioManager.playAudio("back");
                    GameStateManager.sharedInstance().resumeGame();
                    break;
            }
        }
    }

    @Override
    protected void chooseMenuItem() {
        switch (selectedElementIndex) {
            case 0:
                //Resume Game
                AudioManager.playAudio("back");
                GameStateManager.sharedInstance().resumeGame();
                break;
            case 1:
                AudioManager.playAudio("select");
                GameStateManager.sharedInstance().showLegend();
                break;
            case 2:
                AudioManager.playAudio("select");
                GameStateManager.sharedInstance().loadGame();
                break;
            case 3:
                AudioManager.playAudio("select");
                GameStateManager.sharedInstance().saveAndExitGame();
                break;
            case 4:
                AudioManager.playAudio("select");
                GameStateManager.sharedInstance().returnToMainMenu();
                break;
        }
    }

}
