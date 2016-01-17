package com.larsschwegmann.labyrinth.scenes;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.larsschwegmann.labyrinth.AudioManager;
import com.larsschwegmann.labyrinth.GameStateManager;
import com.larsschwegmann.labyrinth.rendering.RenderingToolchain;

public class GameOverMenu extends MainMenu {

    private boolean playerDidWin;
    private boolean redrawStatics = true;
    private Terminal terminal = GameStateManager.sharedInstance().getTerminal();

    public GameOverMenu(boolean playerDidWin) {
        this.playerDidWin = playerDidWin;
        menuElements = null;
    }


    @Override
    public void update() {
        com.googlecode.lanterna.input.Key input = GameStateManager.sharedInstance().readInput();
        if (input != null) {
            MainMenu mm = new MainMenu();
            AudioManager.playAudio("select");
            GameStateManager.sharedInstance().returnToMainMenu();
        }
    }


    @Override
    public void render() {
        super.render();

        if (redrawStatics) {
            int terminalWidth = terminal.getTerminalSize().getColumns();
            int terminalHeight = terminal.getTerminalSize().getRows();

            RenderingToolchain.clearRect(terminal, 3, terminalHeight-4, terminalWidth-5, 2);
            RenderingToolchain.drawString(terminal, "Drücke eine beliebige Taste um zum Hauptmenü zurückzukehren", 3, terminalHeight-3, Terminal.Color.WHITE);

            String message = "";
            if (playerDidWin) {
                message = "Game Over! Glückwunsch, du hast den Ausgang gefunden!";
            } else {
                message = "Game Over! Du hast alle Leben verbraucht! Versuche es noch einmal!";
            }

            RenderingToolchain.drawString(terminal, message, terminalWidth/2-message.length()/2, 12, Terminal.Color.WHITE);

            redrawStatics = false;
        }
    }

    @Override
    public void onResized(TerminalSize terminalSize) {
        super.onResized(terminalSize);
        redrawStatics = true;
    }

}
