package com.larsschwegmann.labyrinth;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.larsschwegmann.labyrinth.level.Level;
import com.larsschwegmann.labyrinth.level.entities.*;

public class Game implements Terminal.ResizeListener, Renderer{

    private Terminal terminal = GameStateManager.sharedInstance().getTerminal();
    private Level level; //current level
    private DynamicTrapUpdateThread trapUpdater;

    //dynamic redrawing
    private boolean redrawStatics = true; //Redraws the level (walls and static objects)
    private boolean redrawStatus = true; //Redraws complete Status window
    private boolean redrawInventory = false;
    private boolean redrawLivesLeft = false;
    private boolean playerDidMove = true;

    //Offset for Scrolling
    private int chunkOffsetX = 0;
    private int chunkOffsetY = 0;

    private int startX;
    private int startY;
    private int limitX;
    private int limitY;

    //Height for the Status Bar (Containing Health and Inventory)
    private final int statusRowSize = 4;

    /*
    Padding == distance to the screen bounds at which the scrolling is supposed to happen
     */
    private final int paddingX = 4; //Padding on the x Axis
    private final int paddingY = 3; //Padding on the y axis

    ////////////////////////////////////////////////////////////////////
    //Getters
    ////////////////////////////////////////////////////////////////////

    public Level getLevel() {
        return level;
    }

    ////////////////////////////////////////////////////////////////////
    //Constructors
    ////////////////////////////////////////////////////////////////////

    public Game(Level l) {
        this.level = l;
        this.trapUpdater = new DynamicTrapUpdateThread(level.getDynamicTraps());
    }

    ////////////////////////////////////////////////////////////////////
    //Game Logic
    ////////////////////////////////////////////////////////////////////

    @Override
    public void update() {
        //Update Positions of dynamic entities and scoll level accordingly
        Player p = level.getPlayer();

        if (p.getLivesLeft() <= 0) {
            //Game over
            GameStateManager.sharedInstance().setCurrentGameState(GameStateManager.GameState.Lost);
        }

        //Update Player Position based on user input
        com.googlecode.lanterna.input.Key input = GameStateManager.sharedInstance().readInput();
        if (input != null) {
            switch (input.getKind()) {
                case ArrowUp:
                    p.move(Player.Direction.Up);
                    playerDidMove = true;
                    break;
                case ArrowRight:
                    p.move(Player.Direction.Right);
                    playerDidMove = true;
                    break;
                case ArrowDown:
                    p.move(Player.Direction.Down);
                    playerDidMove = true;
                    break;
                case ArrowLeft:
                    p.move(Player.Direction.Left);
                    playerDidMove = true;
                    break;
                case Escape:
                    //Pause
                    redrawStatics = true;
                    redrawStatus = true;
                    GameStateManager.sharedInstance().pauseGame();
                    break;
                case NormalKey:
                    //WASD
                    char c = input.getCharacter();
                    if (c == 'w') {
                        p.move(Player.Direction.Up);
                        playerDidMove = true;
                    } else if (c == 'd') {
                        p.move(Player.Direction.Right);
                        playerDidMove = true;
                    } else if (c == 's') {
                        p.move(Player.Direction.Down);
                        playerDidMove = true;
                    } else if (c == 'a') {
                        p.move(Player.Direction.Left);
                        playerDidMove = true;
                    }
                    break;
            }
        }

        //Update dynamic enities
        //Now done by trapUpdater
        /*
        for (DynamicTrap d : level.getDynamicTraps()) {
            d.update();
        }
        */
        //Check entities under player position
        Entity entityAtPlayerPos = level.getEntityAtIndex(p.getX(), p.getY());

        if (entityAtPlayerPos instanceof Key) {
            //Found Key
            level.setEntityAtIndex(null, p.getX(), p.getY());
            p.addToInventory(entityAtPlayerPos);
            redrawInventory = true;
        } else if (entityAtPlayerPos instanceof StaticTrap || entityAtPlayerPos instanceof DynamicTrap) {
            //Stepped on Trap
            p.setLivesLeft(p.getLivesLeft() - 1);
            redrawLivesLeft = true;
        } else if (entityAtPlayerPos instanceof Exit) {
            //Stands on Exit
            if (p.inventoryContainsObjectOfClass(Key.class)) {
                //Only win if player has key for Exit
                GameStateManager.sharedInstance().setCurrentGameState(GameStateManager.GameState.Won);
            } else {
                String message = "Du brauchst einen Schlüssel um den Ausgang zu benutzen!";
                RenderingToolchain.drawString(terminal, message, terminal.getTerminalSize().getColumns() - message.length() - 1, 1, Terminal.Color.RED);
            }
        }

        //Calculate chunkOffsets
        int chunkWidth = getChunkWidth();
        int chunkHeight = getChunkHeight();

        int newChunkOffsetX = p.getX() / chunkWidth;
        int newChunkOffsetY = p.getY() / chunkHeight;

        if (newChunkOffsetX != chunkOffsetX || newChunkOffsetY != chunkOffsetY) {
            redrawStatics = true;
            redrawStatus = true;
            playerDidMove = true;
        }

        chunkOffsetX = newChunkOffsetX;
        chunkOffsetY = newChunkOffsetY;
    }

    ////////////////////////////////////////////////////////////////////
    //Game Rendering
    ////////////////////////////////////////////////////////////////////

    @Override
    public void render() {
        Player p = level.getPlayer();

        int terminalWidth = terminal.getTerminalSize().getColumns();
        int terminalHeight = terminal.getTerminalSize().getRows();

        int chunkWidth = getChunkWidth();
        int chunkHeight = getChunkHeight();

        if (redrawStatics) {
            //Clear old Walls
            RenderingToolchain.clearRect(terminal, 0, statusRowSize, terminalWidth, terminalHeight);
        }

        if (redrawStatus) {
            //Redraw status bar
            RenderingToolchain.clearRect(terminal, 0, 0, terminalWidth, statusRowSize-1);
            RenderingToolchain.drawRect(terminal, 0, 0, terminalWidth, statusRowSize, Terminal.Color.CYAN, '*');
        }

        //Render Status
        if (redrawStatus || redrawLivesLeft) {
            //Redraw Player Health
            RenderingToolchain.clearRect(terminal, 2, 1, 17, 1);
            int hearts = p.getLivesLeft()/10;
            Terminal.Color livesColor = Terminal.Color.RED;
            String livesLeft = "Leben: ";
            for (int i=0; i<hearts; i++) {
                livesLeft += "♥";
            }
            RenderingToolchain.drawString(terminal, livesLeft, 2, 1, livesColor);
            redrawLivesLeft = false;
        }

        if (redrawStatus || redrawInventory) {
            //Redraw Inventory
            RenderingToolchain.clearRect(terminal, 2, 2, terminalWidth - 3, 1);
            RenderingToolchain.drawString(terminal, "Inventar: " + p.getInventory(), 2, 2, Terminal.Color.YELLOW);
            redrawInventory = false;
            redrawStatus = false;
        }

        if (redrawStatics) {
            //Render Level
            //Check if chunk ist first or last chunk

            //Default vlaues for limiters
            int padX = paddingX;
            int padXLimiter = paddingX;
            int padY = paddingY;
            int padYLimiter = paddingY;

            //Start and limiters for 2 dimensional for loop
            int startX;
            int limitX;
            int startY;
            int limitY;

            //We don't want padding on the first and last chunk

            ///Check if we're in the first chuck (x-wise)
            if (chunkOffsetX == 0) {
                //First Chunk
                padX = 0;
                padXLimiter = 0;
            }
            //Check if we're in the first chunk (y-wise)
            if (chunkOffsetY == 0) {
                //First Chunk
                padY = 0;
                padYLimiter = 0;
            }

            startX = chunkOffsetX*chunkWidth-padX;
            limitX = chunkOffsetX*chunkWidth-padXLimiter+terminalWidth;

            //check if we're in the last chunk (x-wise)
            if (chunkOffsetX == level.getWidth()/chunkWidth) {
                startX = this.level.getWidth() - chunkWidth-2*paddingX;
                limitX = this.level.getHeight();
            }

            startY = chunkOffsetY*chunkHeight-padY;
            limitY = chunkOffsetY*chunkHeight-padYLimiter+terminalHeight-statusRowSize;

            //check if we're in the last chunk (y-wise)
            if (chunkOffsetY == level.getHeight()/chunkHeight) {
                startY = this.level.getHeight()-chunkWidth-2*paddingY;
                limitY = this.level.getHeight();
            }

            //Save these values for dynamic entity calculations later on
            //We don't want to recalculate this 30 times per second if the screen is staying the same size und the player doesn't move out of cunk bounds
            this.startX = startX;
            this.startY = startY;
            this.limitX = limitX;
            this.limitY = limitY;

            //Redraw the level
            for (int x=startX; x<limitX; x++) {
                //Iterate ove columns
                for (int y=startY; y<limitY; y++) {
                    //Iterate over rows
                    Entity e = level.getEntityAtIndex(x, y);
                    if (e != null) {
                        if (e.isStatic()) {
                            e.render(terminal, translateX(x), translateY(y));
                        }
                    }
                }
            }
            redrawStatics = false;
        }

        //Render dynamic traps
        for (DynamicTrap d : level.getDynamicTraps()) {
            if (d.getX() >= startX && d.getX() < limitX && d.getY() >= startY && d.getY() < limitY) {
                d.render(terminal, translateX(d.getX()), translateY(d.getY()));
            }
            if (d.getPrevX() >= startX && d.getPrevX() < limitX && d.getPrevY() >= startY && d.getPrevY() < limitY) {
                RenderingToolchain.clearPosition(terminal, translateX(d.getPrevX()), translateY(d.getPrevY()));
            }
        }

        if (playerDidMove) {
            //Rerender tile previously occupied by player
            Entity prevE = level.getEntityAtIndex(p.getPrevX(), p.getPrevY());
            if (prevE != null) {
                prevE.render(terminal, translateX(p.getPrevX()), translateY(p.getPrevY()));
            } else {
                RenderingToolchain.clearPosition(terminal, translateX(p.getPrevX()), translateY(p.getPrevY()));
            }

            //Render Player
            p.render(terminal, translateX(p.getX()), translateY(p.getY()));
            playerDidMove = false;
        }
    }

    /**
     * Translates actual x psotion of level array to position on screen
     * @param x position to translate
     * @return translated x position
     */
    private int translateX(int x) {
        if (chunkOffsetX == 0) {
            //First Chunk
            return x - chunkOffsetX * getChunkWidth();
        } else if (chunkOffsetX == level.getWidth()/getChunkWidth()) {
            //Last chunk
            //return x - chunkOffsetX * getChunkWidth() + 3*paddingX;
            return x - (this.level.getWidth() - getChunkWidth() - 2 * paddingX);
        } else {
            //Inbetween
            return x - chunkOffsetX * getChunkWidth() + paddingX;
        }
    }

    /**
     * Translates actual y postion of level array to position on screen
     * @param y position to translate
     * @return translated y position
     */
    private int translateY(int y) {
        if (chunkOffsetY == 0) {
            //First Chunk
            return y - chunkOffsetY * getChunkHeight() + statusRowSize;
        } else if (chunkOffsetY == level.getHeight()/getChunkHeight()) {
            //Last Chunk
            return y - (this.level.getHeight() - getChunkHeight() - 2 * paddingY) + statusRowSize;
        } else {
            //Inbetween
            return y - chunkOffsetY * getChunkHeight() + paddingY + statusRowSize;
        }
    }

    /**
     * @return width of one chunk
     */
    private int getChunkWidth() {
        return terminal.getTerminalSize().getColumns() - 2 * paddingX;
    }

    /**
     * @return height of one chunk
     */
    private int getChunkHeight() {
        return terminal.getTerminalSize().getRows() - 2 * paddingY - statusRowSize;
    }

    ////////////////////////////////////////////////////////////////////
    //Resize Listener
    ////////////////////////////////////////////////////////////////////

    @Override
    public void onResized(TerminalSize terminalSize) {
        //React to screen reiszement on the next update/render loop
        terminal.clearScreen();
        redrawStatics = true;
        redrawStatus = true;
        playerDidMove = true;
    }

    @Override
    public void willBecomeActiveRenderer() {
        trapUpdater.startUpdating();
        terminal.clearScreen();
        playerDidMove = true;
        terminal.addResizeListener(this);
    }

    @Override
    public void willResignActiveRenderer() {
        trapUpdater.stopUpdating();
        terminal.removeResizeListener(this);
    }

    public void reset() {
        //playerDidMove = true;
        //this.level = null;
    }
    
}
