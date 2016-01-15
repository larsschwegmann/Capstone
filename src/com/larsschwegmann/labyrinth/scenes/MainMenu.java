package com.larsschwegmann.labyrinth.scenes;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.larsschwegmann.labyrinth.GameStateManager;
import com.larsschwegmann.labyrinth.rendering.RenderingToolchain;

import java.util.StringTokenizer;

public class MainMenu implements Renderer, Terminal.ResizeListener{

    private Terminal terminal = GameStateManager.sharedInstance().getTerminal();

    protected String[] menuElements;
    protected int selectedElementIndex = 0;

    private boolean redrawStatics = true;
    private boolean redrawMenu = true;

    protected String titleArt;
    private final int TITLE_Y = 3;
    private final int TITLE_HEIGHT = 6;
    private final int MENU_Y = 3;

    public MainMenu() {
        titleArt =
                "   __       _                _       _   _     \n" +
                "  / /  __ _| |__  _   _ _ __(_)_ __ | |_| |__  \n" +
                " / /  / _` | '_ \\| | | | '__| | '_ \\| __| '_ \\ \n" +
                "/ /__| (_| | |_) | |_| | |  | | | | | |_| | | |\n" +
                "\\____/\\__,_|_.__/ \\__, |_|  |_|_| |_|\\__|_| |_|\n" +
                "                  |___/                        \n";
        this.menuElements = new String[]{"Neues Spiel", "Spiel Laden", "Legende ansehen", "Spiel beenden"};
    }

    @Override
    public void update() {
        //Check Key input
        com.googlecode.lanterna.input.Key input = GameStateManager.sharedInstance().readInput();
        if (input != null && menuElements != null) {
            switch (input.getKind()) {
                case ArrowUp:
                    if (selectedElementIndex > 0) {
                        selectedElementIndex--;
                    }
                    break;
                case ArrowDown:
                    if (selectedElementIndex < menuElements.length-1) {
                        selectedElementIndex++;
                    }
                    break;
                case Enter:
                    chooseMenuItem();
                    break;
                default:
                    break;
            }
            redrawMenu = true;
        }
    }

    @Override
    public void render() {
        int terminalWidth = terminal.getTerminalSize().getColumns();
        int terminalHeight = terminal.getTerminalSize().getRows();

        if (redrawStatics) {
            //Borders
            RenderingToolchain.drawRect(terminal, 0, 0, terminalWidth, terminalHeight, Terminal.Color.CYAN, '*');
            RenderingToolchain.drawRect(terminal, 1, 1, terminalWidth-2, terminalHeight-2, Terminal.Color.RED, '*');

            //Game Title
            Terminal.Color titleColor = Terminal.Color.YELLOW;

            StringTokenizer tk = new StringTokenizer(titleArt, "\n");
            RenderingToolchain.drawASCIIArt(terminal, titleArt, terminalWidth/2-tk.nextToken().length()/2, TITLE_Y, titleColor);

            //Annotations
            RenderingToolchain.drawString(terminal, "Benutze die Pfeiltasten um durch das Menü zu navigieren", 3, terminalHeight-4, Terminal.Color.WHITE);
            RenderingToolchain.drawString(terminal, "Drücke die Entertaste um einen Menüpunkt auszuwählen", 3, terminalHeight-3, Terminal.Color.WHITE);

            redrawStatics = false;
        }

        if (redrawMenu && menuElements != null) {
            Terminal.Color menuColor = Terminal.Color.WHITE;
            int menuWidth = 0;
            int longestElementIndex = 0;
            for (int i=1; i<menuElements.length; i++) {
                if (menuElements[i].length() > menuElements[longestElementIndex].length()) {
                    longestElementIndex = i;
                }
            }
            menuWidth = menuElements[longestElementIndex].length() + 4; //4 is checkbox size
            int menuHeight = menuElements.length;
            int menuX = terminalWidth/2-menuWidth/2;
            int menuY = TITLE_Y+TITLE_HEIGHT + MENU_Y;

            for (int i=0; i<menuElements.length; i++) {
                String strToRender = "";
                if (i == selectedElementIndex) {
                    strToRender += "(*) ";
                } else {
                    strToRender += "( ) ";
                }
                strToRender += menuElements[i];
                RenderingToolchain.drawString(terminal, strToRender, menuX, menuY+i, menuColor);
            }

            redrawMenu = false;
        }
    }

    protected void chooseMenuItem() {
        switch (selectedElementIndex) {
            case 0:
                //New Game
                GameStateManager.sharedInstance().startNewGame();
                break;
            case 1:
                GameStateManager.sharedInstance().loadGame();
                break;
            case 2:
                GameStateManager.sharedInstance().showLegend();
                break;
            case 3:
                GameStateManager.sharedInstance().exitGame();
                break;
        }
    }

    @Override
    public void willBecomeActiveRenderer() {
        terminal.clearScreen();
        redrawMenu = true;
        redrawStatics = true;
        terminal.addResizeListener(this);
    }

    @Override
    public void willResignActiveRenderer() {
        terminal.removeResizeListener(this);
    }

    @Override
    public void onResized(TerminalSize terminalSize) {
        terminal.clearScreen();
        redrawStatics = true;
        redrawMenu = true;
    }
}
