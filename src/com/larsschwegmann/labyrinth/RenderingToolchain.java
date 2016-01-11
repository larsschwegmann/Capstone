package com.larsschwegmann.labyrinth;

import com.googlecode.lanterna.terminal.Terminal;

import java.util.StringTokenizer;

public class RenderingToolchain {

    ////////////////////////////////////////////////////////////////////
    //Game Rendering Shortcuts
    ////////////////////////////////////////////////////////////////////

    public static void drawRect(Terminal terminal, int x, int y, int w, int h, Terminal.Color c, char symbol) {
        terminal.moveCursor(x, y);
        terminal.applyForegroundColor(c);
        for (int i=0; i<w; i++) {
            terminal.putCharacter(symbol);
        }
        terminal.moveCursor(x, y + h-1);
        for (int i=0; i<w; i++) {
            terminal.putCharacter(symbol);
        }
        for (int i=y+1; i<h; i++) {
            terminal.moveCursor(x, i);
            terminal.putCharacter(symbol);
            terminal.moveCursor(x+w-1, i);
            terminal.putCharacter(symbol);
        }
    }

    public static void clearRect(Terminal terminal, int x, int y, int w, int h) {
        for (int r=y; r<y+h; r++) {
            terminal.moveCursor(x, r);
            for (int c=0; c<w; c++) {
                terminal.putCharacter(' ');
            }
        }
    }

    public static void drawString(Terminal terminal, String str, int x, int y, Terminal.Color fg) {
        terminal.moveCursor(x, y);
        terminal.applyForegroundColor(fg);
        for (int i=0; i<str.length(); i++) {
            terminal.putCharacter(str.charAt(i));
        }
    }

    public static void drawASCIIArt(Terminal terminal, String art, int x, int y, Terminal.Color c) {
        StringTokenizer tk = new StringTokenizer(art, "\n");
        int rowCount = 0;
        while (tk.hasMoreTokens()) {
            String row = tk.nextToken();
            drawString(terminal, row, x, y+rowCount, c);
            rowCount++;
        }
    }

    public static void clearPosition(Terminal terminal, int x, int y) {
        terminal.moveCursor(x, y);
        terminal.putCharacter(' ');
    }

}
