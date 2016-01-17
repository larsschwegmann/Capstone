package com.larsschwegmann.labyrinth;

import com.larsschwegmann.labyrinth.scenes.MainMenu;
import com.larsschwegmann.labyrinth.scenes.PauseMenu;
import com.larsschwegmann.labyrinth.scenes.GameOverMenu;
import com.larsschwegmann.labyrinth.scenes.Game;
import com.larsschwegmann.labyrinth.scenes.Renderer;
import com.larsschwegmann.labyrinth.scenes.LegendMenu;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.larsschwegmann.labyrinth.level.Level;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

public class GameStateManager {

    /**
     * Describes all possible Game States
     */
    public enum GameState {
        MainMenu,
        Load,
        Save,
        Legend,
        Playing,
        Paused,
        Lost,
        Won,
        Quit
    }

    ////////////////////////////////////////////////////////////////////
    //Singleton Methods
    ////////////////////////////////////////////////////////////////////

    private static GameStateManager sharedGameStateManager;

    /**
     * @return a shared Instance of GameStateMAnager that can be accessed throughout the whole Game from anywhere
     */
    public static GameStateManager sharedInstance() {
        if (sharedGameStateManager == null) {
            sharedGameStateManager = new GameStateManager();
        }
        return sharedGameStateManager;
    }

    ////////////////////////////////////////////////////////////////////
    //Attributes
    ////////////////////////////////////////////////////////////////////

    //Save the current Game State
    private GameState currentGameState;

    //The terminal that is rendered to
    private Terminal terminal;

    //Instance of the Game class
    private Game game;

    //The active (frontmost) render
    private Renderer activeRenderer;

    //Rendering stuff
    private final int FPS = 30;
    private final int SKIP_FRAMES = 1000/FPS;
    private long time = System.currentTimeMillis();
    private long sleepTime = 0;
    private long lastTime = 0;
    private long lastFPSDisplayTime = 0;
    private final boolean DEBUG = true;

    //Custom global keybuffer stuff
    private Key lastInput;

    //Necessary for returning to the correct menu after shwoing the Game Legend
    private MainMenu lastShownMenu;

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public void setCurrentGameState(GameState currentGameState) {
        this.currentGameState = currentGameState;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Game getGame() {
        return game;
    }

    ////////////////////////////////////////////////////////////////////
    //Constructors
    ////////////////////////////////////////////////////////////////////

    private GameStateManager() {
        this.currentGameState = GameState.MainMenu;
        this.terminal = TerminalFacade.createTerminal(Charset.forName("UTF-16"));
        this.terminal.setCursorVisible(false);
        this.terminal.enterPrivateMode();
        Level.getSaveFilePathForName("");
        lastInput = null;
        if (terminal instanceof SwingTerminal) {
            //We have a windowed terminal, so we can set things such as title and size
            SwingTerminal sTerminal = (SwingTerminal) terminal;
            sTerminal.getJFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            sTerminal.getJFrame().setMinimumSize(new Dimension(sTerminal.getJFrame().getWidth(), sTerminal.getJFrame().getHeight()));
            sTerminal.getJFrame().setTitle("Labyrinth");
            //Center terminal window
            sTerminal.getJFrame().setLocationRelativeTo(null);
        }
    }

    ////////////////////////////////////////////////////////////////////
    //Other Methods
    ////////////////////////////////////////////////////////////////////

    /**
     * Called on startup by Main.
     * Starts the Menu
     */
    public void boot() {
        //Startup
        //Show main menu
        MainMenu mm = new MainMenu();
        lastShownMenu = mm;
        setActiveRenderer(mm);

        //Start render loop
        try {
            this.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Runs the Game Loop
     * calls update() and render() on the activeRenderer
     * also maintains the GameStateManager Keybuffer
     * @throws Exception
     */
    public void run() throws Exception {

        lastTime = System.nanoTime();
        lastFPSDisplayTime = System.currentTimeMillis();

        while (this.currentGameState != GameState.Quit) {
            //Game Loop
            //Look for input
            //If Won or Lost => Show Game Over menu
            if (this.currentGameState == GameState.Won) {
                this.currentGameState = GameState.MainMenu;
                GameOverMenu gom = new GameOverMenu(true);
                setActiveRenderer(gom);
            } else if (this.currentGameState == GameState.Lost) {
                this.currentGameState = GameState.MainMenu;
                GameOverMenu gom = new GameOverMenu(false);
                setActiveRenderer(gom);
            }

            //Update Keybuffer
            com.googlecode.lanterna.input.Key input = terminal.readInput();
            this.lastInput = input;
            
            activeRenderer.update();
            activeRenderer.render();
            
            if (DEBUG) {
                try {
                    //Prevent divisons by 0 in Frame counter
                    Thread.sleep(5);
                } catch (Exception ex) {
                }
                //FPS Counter
                int fps = 0;
                if (1000000000/(System.nanoTime() - lastTime) < 100) {
                    fps = new Double(1000000000/(System.nanoTime() - lastTime)).intValue();
                }
                lastTime = System.nanoTime();

                if (System.currentTimeMillis() - lastFPSDisplayTime >= 500) {
                    if (terminal instanceof SwingTerminal) {
                        SwingTerminal sTerminal = (SwingTerminal) terminal;
                        sTerminal.getJFrame().setTitle("Labyrinth - FPS: " + fps);
                    }
                    lastFPSDisplayTime = System.currentTimeMillis();
                }
            }

            //FPS Limiter
            time += SKIP_FRAMES;
            sleepTime = time - System.currentTimeMillis();
            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
        }
        //Game State == Quit
        System.exit(0);
    }

    /**
     * Sets activeRender to specified Renderer r
     * @param r the new activeRender
     */
    public void setActiveRenderer(Renderer r) {
        if (this.activeRenderer != null) {
            //notify old active Renderer
            this.activeRenderer.willResignActiveRenderer();
        }
        if (r != null) {
            //Notify new active Renderer
            r.willBecomeActiveRenderer();
            this.activeRenderer = r; //Sets active renderer
        }
    }

    /**
     * @return last input from Key Buffer
     */
    public Key readInput() {
        return lastInput;
    }

    /**
     * Starts a new Game and displays it onscreen
     */
    public void startNewGame() {
        try {
            Level lvl = new Level("level_small.properties");
            lvl.load();
            game = new Game(lvl);
            game.reset();
            this.currentGameState = GameState.Playing;
            setActiveRenderer(game);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            showError("Level wurde nicht gefunden! Bitte README.md lesen!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Pauses the game and displays pause menu
     */
    public void pauseGame() {
        this.currentGameState = GameState.Paused;
        PauseMenu p = new PauseMenu();
        lastShownMenu = p;
        setActiveRenderer(p);
    }

    /**
     * Resumes Game and hides pause menu
     */
    public void resumeGame() {
        this.currentGameState = GameState.Playing;
        setActiveRenderer(game);
    }

    /**
     * Loads a game from save file and displays the game scene
     */
    public void loadGame() {
        this.currentGameState = GameState.Load;
        try {
            //Load level
            Level lvl = new Level("level_saved.properties");
            lvl.load();
            this.game = new Game(lvl);
            this.game.reset(); //Resets Game Scene
            this.currentGameState = GameState.Playing;
            setActiveRenderer(game);
        } catch (FileNotFoundException ex) {
            //Level not found
            ex.printStackTrace();
            showError("Kein gepeichertes Spiel gefunden! Bitte README.md lesen!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the current game state to disk and return to the main menu
     */
    public void saveAndExitGame() {
        this.currentGameState = GameState.Save;
        try {
            this.game.getLevel().store(); //Save game
            MainMenu mm = new MainMenu(); //Return to main menu
            lastShownMenu = mm;
            setActiveRenderer(mm);
        } catch (Exception ex) {
            //Couldn't write file
            ex.printStackTrace();
            showError("Spielstand konnte nicht gepeichert werden!");
        }
    }

    /**
     * Returns to main menu without saving
     */
    public void returnToMainMenu() {
        MainMenu mm = new MainMenu();
        lastShownMenu = mm;
        setActiveRenderer(mm);
    }

    /**
     * Sets Game State to Quit. Application will quit after finishing current game loop.
     */
    public void exitGame() {
        this.currentGameState = GameState.Quit;
    }

    /**
     * Displays the game legend
     */
    public void showLegend() {
        this.currentGameState = GameState.Legend;
        LegendMenu lm = new LegendMenu();
        setActiveRenderer(lm);
    }

    /**
     * Hides the game legend
     */
    public void hideLegend() {
        if (lastShownMenu instanceof PauseMenu) {
            this.currentGameState = GameState.Paused;
        } else {
            this.currentGameState = GameState.MainMenu;
        }
        setActiveRenderer(lastShownMenu);
    }

    /**
     * Shos a JOptionPane to the user displaying the given Error Message msg
     * @param msg an Error message
     */
    private void showError(String msg) {
        if (terminal instanceof SwingTerminal) {
            SwingTerminal sTerminal = (SwingTerminal) terminal;
            JOptionPane.showMessageDialog(sTerminal.getJFrame(), msg);
        }
    }

}
