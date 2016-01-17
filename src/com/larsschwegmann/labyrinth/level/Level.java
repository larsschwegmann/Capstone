package com.larsschwegmann.labyrinth.level;

import com.larsschwegmann.labyrinth.level.entities.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

public class Level {

    private final String WIDTH_KEY = "Width";
    private final String HEIGHT_KEY = "Height";
    private final String PLAYER_KEY = "Player";
    private final String PLAYER_LIVES_KEY = "PlayerLivesLeft";
    private final String PLAYER_INVENTORY_KEY = "PlayerKeyAmount";

    //Contains the raw Properties file
    private Properties rawLevelData;

    //Contains the Level Entities
    private Entity[][] levelData;
    //Contains the filepat relative to the .labyrinth savefile directory
    private String path;

    //Contains width and height of the level
    private int width;
    private int height;

    private Player player;

    //Contains all dynamic traps inside of the level
    private ArrayList<DynamicTrap> dynamicTraps;

    public Player getPlayer() {
        return player;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<DynamicTrap> getDynamicTraps() {
        return dynamicTraps;
    }

    /**
     * Gets the Entity at the specified coordinates (x,y)
     * @param x x Positon of entity that should be returned
     * @param y y Positon of entity that should be returned
     * @return Entity at Position
     */
    public Entity getEntityAtIndex(int x, int y) {
        if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
            return levelData[x][y];
        } else {
            return null;
        }
    }

    /**
     * Sets entity at positon (x,y) to e
     * @param e new entity
     * @param x x Positon of entity that should be replaced
     * @param y y Positon of entity that should be replaced
     */
    public void setEntityAtIndex(Entity e, int x, int y) {
        this.levelData[x][y] = e;
    }

    /**
     * Constructor
     * @param path path to the level file relative to the .labyrinth savefile directory
     */
    public Level(String path) {
        this.path = path;
        this.dynamicTraps = new ArrayList();
    }

    /**
     * Loads a savefile at this.path. Can be an actual "save" or simply a level made by the Generator.
     * @throws Exception
     */
    public void load() throws Exception {
        File fileToLoad = new File(getSaveFilePathForName(this.path));
        if (!fileToLoad.exists()) {
            throw new FileNotFoundException(fileToLoad.getAbsolutePath() + " doesn't exist.");
        }

        boolean isSavedLevel = this.path.contains("saved");
        InputStream in = new FileInputStream(fileToLoad);
        rawLevelData = new Properties();
        rawLevelData.load(in);
        width = Integer.parseInt(rawLevelData.getProperty(WIDTH_KEY));
        height = Integer.parseInt(rawLevelData.getProperty(HEIGHT_KEY));
        levelData = new Entity[width][height];
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if (rawLevelData.getProperty(x + "," + y) != null) {
                    int rawEntityValueAtPos = Integer.parseInt(rawLevelData.getProperty(x + "," + y));
                    levelData[x][y] = EntityFactory.createEntity(rawEntityValueAtPos, x, y);
                    if (levelData[x][y] instanceof Entrance && !isSavedLevel) {
                        player = EntityFactory.createPlayer(x, y);
                    } else if (levelData[x][y] instanceof DynamicTrap) {
                        dynamicTraps.add((DynamicTrap)levelData[x][y]);
                    }
                }
            }
        }
        if (isSavedLevel) {
            String playerCoordinatesString = rawLevelData.getProperty(PLAYER_KEY);
            String[] coordinates = playerCoordinatesString.split(",");
            int playerX = Integer.parseInt(coordinates[0]);
            int playerY = Integer.parseInt(coordinates[1]);
            player = EntityFactory.createPlayer(playerX, playerY);
            player.setLivesLeft(Integer.parseInt(rawLevelData.getProperty(PLAYER_LIVES_KEY)));
            int keyAmount = Integer.parseInt(rawLevelData.getProperty(PLAYER_INVENTORY_KEY));
            for (int i=0; i<keyAmount; i++) {
                Key k = new Key(0, 0);
                player.addToInventory(k);
            }
        }
    }

    /**
     * Stores the level and player attributes inside a savefile
     * @throws Exception
     */
    public void store() throws Exception {
        OutputStream out = new FileOutputStream(getSaveFilePathForName("level_saved.properties"));
        Properties props = new Properties();
        props.setProperty(WIDTH_KEY, this.width + "");
        props.setProperty(HEIGHT_KEY, this.height + "");
        props.setProperty(PLAYER_KEY, player.getX() + "," + player.getY());
        props.setProperty(PLAYER_LIVES_KEY, player.getLivesLeft() + "");
        props.setProperty(PLAYER_INVENTORY_KEY, player.getInventory().size() + "");
        for (int x=0; x<this.width; x++) {
            for (int y=0; y<this.height; y++) {
                if (levelData[x][y] != null) {
                    props.setProperty(x + "," + y, levelData[x][y].getRawValue() + "");
                }
            }
        }
        props.store(out, "Saved on " + System.currentTimeMillis());
    }

    /**
     * Translates a filename into the actual name on disc in the savefile directory
     * @param filename the filename to translate
     * @return translated file path (absolute file path)
     */
    public static String getSaveFilePathForName(String filename) {
        String userHomePath = System.getProperty("user.home");
        String appDirectory = userHomePath + File.separator + ".labyrinth";
        File appDirectoryFile = new File(appDirectory);
        if (!appDirectoryFile.exists()) {
            appDirectoryFile.mkdir();
        }
        return appDirectory + File.separator + filename;
    }

}
