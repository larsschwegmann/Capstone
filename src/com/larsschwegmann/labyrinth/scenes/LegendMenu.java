package com.larsschwegmann.labyrinth.scenes;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.larsschwegmann.labyrinth.GameStateManager;
import com.larsschwegmann.labyrinth.rendering.RenderingToolchain;

import java.util.StringTokenizer;

public class LegendMenu extends MainMenu {

    private boolean redrawStatics = true;
    private Terminal terminal = GameStateManager.sharedInstance().getTerminal();

    public LegendMenu() {
        menuElements = null;
    }


    @Override
    public void update() {
        com.googlecode.lanterna.input.Key input = GameStateManager.sharedInstance().readInput();
        if (input != null) {
            //MainMenu mm = new MainMenu();
            GameStateManager.sharedInstance().hideLegend();
        }
    }


    @Override
    public void render() {
        super.render();

        if (redrawStatics) {
            int terminalWidth = terminal.getTerminalSize().getColumns();
            int terminalHeight = terminal.getTerminalSize().getRows();

            RenderingToolchain.clearRect(terminal, 3, terminalHeight-4, terminalWidth-5, 2);
            RenderingToolchain.drawString(terminal, "Drücke eine beliebige Taste um zum vorherigen Menü zurückzukehren", 3, terminalHeight-3, Terminal.Color.WHITE);

            String introMessage = "Benutze die Pfeiltasten, um durch das Level zu navigieren.";
            int messageY = 12;
            RenderingToolchain.drawString(terminal, introMessage, terminalWidth/2-introMessage.length()/2, messageY, Terminal.Color.WHITE);
            for (int i=0; i<7; i++) {
                String label = "";
                terminal.moveCursor(terminalWidth/2-introMessage.length()/2, messageY + i + 2);
                switch (i) {
                    case 0:
                        terminal.applyForegroundColor(Terminal.Color.GREEN);
                        terminal.putCharacter('\u263A');
                        label = "Spieler";
                        break;
                    case 1:
                        terminal.applyForegroundColor(Terminal.Color.WHITE);
                        terminal.putCharacter('\u2593');
                        label = "Wand";
                        break;
                    case 2:
                        terminal.applyForegroundColor(Terminal.Color.WHITE);
                        terminal.putCharacter('S');
                        label = "Eingang";
                        break;
                    case 3:
                        terminal.applyForegroundColor(Terminal.Color.CYAN);
                        terminal.putCharacter('E');
                        label = "Ausgang";
                        break;
                    case 4:
                        terminal.applyForegroundColor(Terminal.Color.YELLOW);
                        terminal.putCharacter('K');
                        label = "Schlüssel";
                        break;
                    case 5:
                        terminal.applyForegroundColor(Terminal.Color.RED);
                        terminal.putCharacter('F');
                        label = "Statisches Hindernis";
                        break;
                    case 6:
                        terminal.applyForegroundColor(Terminal.Color.RED);
                        terminal.putCharacter('D');
                        label = "Dynamisches Hindernis";
                        break;
                }
                terminal.moveCursor(terminalWidth/2 + introMessage.length()/2 - label.length(), messageY + i + 2);
                for (int x=0; x<label.length(); x++) {
                    terminal.putCharacter(label.charAt(x));
                }
            }

            redrawStatics = false;
        }
    }

    @Override
    public void onResized(TerminalSize terminalSize) {
        super.onResized(terminalSize);
        redrawStatics = true;
    }

}
