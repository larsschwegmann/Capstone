package com.larsschwegmann.labyrinth;

public interface Renderer {

    /**
     * Called by GameStateManager
     * Use this method to update positions and recalculate things
     */
    public void update();

    /**
     * Called by GameStateManager after update()
     * Use this method to draw on the screen
     */
    public void render();

    /**
     * Called by GameStateManager when you're the new activeRenderer
     */
    public void willBecomeActiveRenderer();

    /**
     * Called by GameStateManager when you'll be removed as the activeRenderer
     */
    public void willResignActiveRenderer();

}
