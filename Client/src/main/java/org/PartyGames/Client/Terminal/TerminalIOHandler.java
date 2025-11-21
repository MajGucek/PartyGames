package org.PartyGames.Client.Terminal;


import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerminalIOHandler {
    private static final Logger logger = LoggerFactory.getLogger(TerminalIOHandler.class);
    // This is purely for rendering,
    private Screen screen;
    // modify "graphics" and then render the whole thing with screen.refresh()
    private TextGraphics graphics;
    private boolean started;

    public TerminalIOHandler() {
        screen = null;
        started = false;
        graphics = null;
    }


    public void start() {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        try {
            Terminal terminal = defaultTerminalFactory.createTerminal();
            screen = new TerminalScreen(terminal);
            started = true;
            screen.startScreen();
            hideCursor();
            graphics = screen.newTextGraphics();
        } catch (IOException e) {
            logger.error("Error starting: {}", String.valueOf(e));
        }
    }


    public void stop() {
        try {
            screen.stopScreen();
            started = false;
        } catch (IOException e) {
            logger.error("Error stopping: {}", String.valueOf(e));
        }
    }


    public void clearScreen() {
        if (screen != null) {
            screen.clear();
        } else {
            logger.error("Cannot clear screen, because screen == null!");
        }
    }


    public void drawSquare(int x, int y, int w, int h, String color) {
        graphics.setBackgroundColor(getRGB(color));
        graphics.fillRectangle(new TerminalPosition(x, y), new TerminalSize(w, h), ' ');
    }


    public void drawText(int x, int y, String text, String color) {
        graphics.setForegroundColor(getRGB(color));
        graphics.putString(new TerminalPosition(x, y), text);
    }


    public void hideCursor() {
        if (screen != null) {
            screen.setCursorPosition(null);
        } else {
            logger.error("Cannot hide cursor, because screen == null!");
        }
    }


    public void setCursor(int x, int y) {
        if (screen != null) {
            screen.setCursorPosition(new TerminalPosition(x, y));
        } else {
            logger.error("Cannot set Cursor, because screen == null!");
        }

    }


    public void render() {
        try {
            screen.refresh();
        } catch (IOException e) {
            logger.error("Error refreshing screen: {}", String.valueOf(e));
        }
    }


    public KeyStroke poll() {
        try {
            return screen.pollInput();
        } catch (IOException e) {
            logger.error("Error polling inputs: {}", String.valueOf(e));
        }
        return null;
    }

    // Source - https://stackoverflow.com/a/7614202
    // Posted by Confluence, modified by community. See post 'Timeline' for change history
    // Retrieved 2025-11-06, License - CC BY-SA 3.0
    public static TextColor.RGB getRGB(String input)
    {
        Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
        Matcher m = c.matcher(input);

        if (m.matches())
        {
            return new TextColor.RGB(
                    Integer.parseInt(m.group(1)),  // r
                    Integer.parseInt(m.group(2)),  // g
                    Integer.parseInt(m.group(3))); // b
        }
        logger.error("The input isn't formatted correctly! {}", input);
        return null;
    }

}
